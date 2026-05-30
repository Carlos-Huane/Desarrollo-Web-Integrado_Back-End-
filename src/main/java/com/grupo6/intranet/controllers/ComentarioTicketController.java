package com.grupo6.intranet.controllers;

import com.grupo6.intranet.dtos.ComentarioRequest;
import com.grupo6.intranet.models.ComentarioTicket;
import com.grupo6.intranet.services.ComentarioTicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "*")
public class ComentarioTicketController {

    @Autowired
    private ComentarioTicketService comentarioService;

    @GetMapping("/ticket/{ticketId}")
    public List<ComentarioTicket> listarPorTicket(
            @PathVariable Long ticketId,
            @RequestParam(name = "incluirInternos", defaultValue = "false") boolean incluirInternos) {
        return comentarioService.listarPorTicket(ticketId, incluirInternos);
    }

    @GetMapping("/ticket/{ticketId}/conteo")
    public Map<String, Long> contarPorTicket(@PathVariable Long ticketId) {
        return Map.of("total", comentarioService.contarPorTicket(ticketId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioTicket> buscarPorId(@PathVariable Long id) {
        return comentarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/ticket/{ticketId}/usuario/{autorId}")
    public ResponseEntity<ComentarioTicket> crear(
            @PathVariable Long ticketId,
            @PathVariable Long autorId,
            @Valid @RequestBody ComentarioRequest req) {
        return ResponseEntity.ok(comentarioService.crear(ticketId, autorId, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return comentarioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
