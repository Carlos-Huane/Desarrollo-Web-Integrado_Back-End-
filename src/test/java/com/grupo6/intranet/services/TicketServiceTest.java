package com.grupo6.intranet.services;

import com.grupo6.intranet.dtos.CambioEstadoRequest;
import com.grupo6.intranet.models.Estado;
import com.grupo6.intranet.models.Ticket;
import com.grupo6.intranet.models.Usuario;
import com.grupo6.intranet.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private HistorialTicketRepository historialRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private SubcategoriaRepository subcategoriaRepository;
    @Mock private EmailService emailService;

    @InjectMocks private TicketService ticketService;

    @Test
    void asignarTecnicoDesdeEstadoNuevoDebeMoverElTicketAAtencion() {
        Ticket ticket = new Ticket();
        ticket.setId(10L);
        ticket.setEstado(Estado.NUEVO);

        Usuario admin = new Usuario();
        admin.setId(1L);

        Usuario tecnico = new Usuario();
        tecnico.setId(2L);

        CambioEstadoRequest req = new CambioEstadoRequest();
        req.setEstadoNuevo(Estado.NUEVO);
        req.setTecnicoId(2L);
        req.setComentario("Asignado para revisión");

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(tecnico));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Ticket> actualizado = ticketService.cambiarEstado(10L, req, 1L);

        assertTrue(actualizado.isPresent());
        assertEquals(Estado.EN_ATENCION, actualizado.get().getEstado());
        assertEquals(tecnico, actualizado.get().getTecnico());
    }
}
