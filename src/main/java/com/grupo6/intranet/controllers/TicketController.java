package com.grupo6.intranet.controllers;

import com.grupo6.intranet.dtos.CambioEstadoRequest;
import com.grupo6.intranet.dtos.TicketRequest;
import com.grupo6.intranet.models.HistorialTicket;
import com.grupo6.intranet.models.Ticket;
import com.grupo6.intranet.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> listarTodos() {
        return ticketService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> buscarPorId(@PathVariable Long id) {
        return ticketService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Ticket> listarPorCliente(@PathVariable Long clienteId) {
        return ticketService.listarPorCliente(clienteId);
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<Ticket> listarPorTecnico(@PathVariable Long tecnicoId) {
        return ticketService.listarPorTecnico(tecnicoId);
    }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Ticket> crear(
            @Valid @RequestBody TicketRequest req,
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(ticketService.crear(req, clienteId));
    }

    @PatchMapping("/{ticketId}/estado/{usuarioId}")
    public ResponseEntity<Ticket> cambiarEstado(
            @PathVariable Long ticketId,
            @PathVariable Long usuarioId,
            @Valid @RequestBody CambioEstadoRequest req) {
        return ticketService.cambiarEstado(ticketId, req, usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{ticketId}/historial")
    public List<HistorialTicket> obtenerHistorial(@PathVariable Long ticketId) {
        return ticketService.obtenerHistorial(ticketId);
    }
}
