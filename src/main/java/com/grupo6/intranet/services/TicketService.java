package com.grupo6.intranet.services;

import com.grupo6.intranet.dtos.CambioEstadoRequest;
import com.grupo6.intranet.dtos.TicketRequest;
import com.grupo6.intranet.models.*;
import com.grupo6.intranet.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private HistorialTicketRepository historialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    @Autowired
    private EmailService emailService;

    public Ticket crear(TicketRequest req, Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Categoria categoria = categoriaRepository.findById(req.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Ticket ticket = new Ticket();
        ticket.setTitulo(req.getTitulo());
        ticket.setDescripcion(req.getDescripcion());
        ticket.setCliente(cliente);
        ticket.setCategoria(categoria);
        ticket.setPrioridad(req.getPrioridad() != null ? req.getPrioridad() : Prioridad.SIN_ASIGNAR);
        ticket.setEstado(Estado.NUEVO);

        if (req.getSubcategoriaId() != null) {
            subcategoriaRepository.findById(req.getSubcategoriaId())
                    .ifPresent(ticket::setSubcategoria);
        }

        Ticket guardado = ticketRepository.save(ticket);
        registrarHistorial(guardado, cliente, null, Estado.NUEVO, "Ticket creado");
        emailService.notificarTicketCreado(guardado);
        return guardado;
    }

    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    public List<Ticket> listarPorCliente(Long clienteId) {
        return ticketRepository.findByClienteId(clienteId);
    }

    public List<Ticket> listarPorTecnico(Long tecnicoId) {
        return ticketRepository.findByTecnicoIdAndEstadoNot(tecnicoId, Estado.CERRADO);
    }

    public Optional<Ticket> buscarPorId(Long id) {
        return ticketRepository.findById(id);
    }

    public Optional<Ticket> cambiarEstado(Long ticketId, CambioEstadoRequest req, Long usuarioId) {
        return ticketRepository.findById(ticketId).map(ticket -> {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Estado estadoAnterior = ticket.getEstado();
            Long tecnicoAnteriorId = ticket.getTecnico() != null ? ticket.getTecnico().getId() : null;
            ticket.setEstado(req.getEstadoNuevo());

            boolean tecnicoCambio = false;
            if (req.getTecnicoId() != null) {
                Optional<Usuario> nuevoTecnico = usuarioRepository.findById(req.getTecnicoId());
                if (nuevoTecnico.isPresent() && !req.getTecnicoId().equals(tecnicoAnteriorId)) {
                    ticket.setTecnico(nuevoTecnico.get());
                    tecnicoCambio = true;
                }
            }

            if (req.getEstadoNuevo() == Estado.RESUELTO) {
                ticket.setFechaResolucion(LocalDateTime.now());
            }

            Ticket actualizado = ticketRepository.save(ticket);
            registrarHistorial(actualizado, usuario, estadoAnterior, req.getEstadoNuevo(), req.getComentario());

            if (tecnicoCambio) {
                emailService.notificarAsignacionTecnico(actualizado);
            }
            emailService.notificarCambioEstado(actualizado, estadoAnterior, req.getEstadoNuevo(), req.getComentario());
            return actualizado;
        });
    }

    public List<HistorialTicket> obtenerHistorial(Long ticketId) {
        return historialRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    private void registrarHistorial(Ticket ticket, Usuario usuario, Estado anterior, Estado nuevo, String comentario) {
        HistorialTicket h = new HistorialTicket();
        h.setTicket(ticket);
        h.setUsuario(usuario);
        h.setEstadoAnterior(anterior);
        h.setEstadoNuevo(nuevo);
        h.setComentario(comentario);
        historialRepository.save(h);
    }
}
