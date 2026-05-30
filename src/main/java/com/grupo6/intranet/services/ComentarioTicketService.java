package com.grupo6.intranet.services;

import com.grupo6.intranet.dtos.ComentarioRequest;
import com.grupo6.intranet.models.ComentarioTicket;
import com.grupo6.intranet.models.Rol;
import com.grupo6.intranet.models.Ticket;
import com.grupo6.intranet.models.Usuario;
import com.grupo6.intranet.repositories.ComentarioTicketRepository;
import com.grupo6.intranet.repositories.TicketRepository;
import com.grupo6.intranet.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ComentarioTicketService {

    @Autowired
    private ComentarioTicketRepository comentarioRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public ComentarioTicket crear(Long ticketId, Long autorId, ComentarioRequest req) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esInterno = Boolean.TRUE.equals(req.getInterno());
        if (esInterno && autor.getRol() == Rol.CLIENTE) {
            throw new RuntimeException("Un cliente no puede crear comentarios internos");
        }

        ComentarioTicket c = new ComentarioTicket();
        c.setTicket(ticket);
        c.setAutor(autor);
        c.setMensaje(req.getMensaje());
        c.setInterno(esInterno);
        return comentarioRepository.save(c);
    }

    public List<ComentarioTicket> listarPorTicket(Long ticketId, boolean incluirInternos) {
        if (incluirInternos) {
            return comentarioRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        }
        return comentarioRepository.findByTicketIdAndInternoFalseOrderByCreatedAtAsc(ticketId);
    }

    public Optional<ComentarioTicket> buscarPorId(Long id) {
        return comentarioRepository.findById(id);
    }

    public long contarPorTicket(Long ticketId) {
        return comentarioRepository.countByTicketId(ticketId);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (comentarioRepository.existsById(id)) {
            comentarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
