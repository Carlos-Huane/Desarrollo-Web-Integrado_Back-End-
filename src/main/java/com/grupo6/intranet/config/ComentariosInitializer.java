package com.grupo6.intranet.config;

import com.grupo6.intranet.models.ComentarioTicket;
import com.grupo6.intranet.models.Ticket;
import com.grupo6.intranet.models.Usuario;
import com.grupo6.intranet.repositories.ComentarioTicketRepository;
import com.grupo6.intranet.repositories.TicketRepository;
import com.grupo6.intranet.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class ComentariosInitializer implements CommandLineRunner {

    @Autowired private ComentarioTicketRepository comentarioRepo;
    @Autowired private TicketRepository ticketRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    @Override
    public void run(String... args) {
        if (comentarioRepo.count() > 0) return;
        if (ticketRepo.count() == 0 || usuarioRepo.count() == 0) return;

        Usuario admin1 = usuarioRepo.findByEmail("admin@telecoperu.com").orElse(null);
        Usuario tec1   = usuarioRepo.findByEmail("tecnico1@telecoperu.com").orElse(null);
        Usuario tec2   = usuarioRepo.findByEmail("tecnico2@telecoperu.com").orElse(null);
        Usuario cli1   = usuarioRepo.findByEmail("pedro.sanchez@telecoperu.com").orElse(null);
        Usuario cli2   = usuarioRepo.findByEmail("maria.lopez@telecoperu.com").orElse(null);
        Usuario cli3   = usuarioRepo.findByEmail("jose.quispe@telecoperu.com").orElse(null);

        if (admin1 == null || tec1 == null || tec2 == null) return;

        Ticket t1 = ticketRepo.findById(1L).orElse(null);
        Ticket t3 = ticketRepo.findById(3L).orElse(null);
        Ticket t5 = ticketRepo.findById(5L).orElse(null);
        Ticket t6 = ticketRepo.findById(6L).orElse(null);

        if (t1 != null && cli1 != null) {
            comentario(t1, cli1, "Sigue muy lento, ya intenté reiniciar el router pero no mejora.", false);
            comentario(t1, tec1, "Confirmado, ya estoy revisando el switch del piso 3. Te aviso en cuanto identifique.", false);
            comentario(t1, tec1, "Cliente VIP — priorizar este caso sobre los demás de la cola.", true);
        }
        if (t3 != null && cli1 != null) {
            comentario(t3, cli1, "El error aparece justo al abrir la sección de facturas emitidas.", false);
            comentario(t3, tec2, "Replicado en mi equipo, parece ser un problema de caché. Probando solución del KB.", false);
            comentario(t3, tec2, "Bug confirmado en FacturaSoft 4.2 — escalar al proveedor si no resuelve con caché.", true);
        }
        if (t5 != null && cli2 != null) {
            comentario(t5, tec2, "Equipo escaneado completo, troyano eliminado y actualizado el antivirus.", false);
            comentario(t5, cli2, "Muchas gracias, todo funcionando bien ahora.", false);
        }
        if (t6 != null && cli3 != null) {
            comentario(t6, tec1, "Permisos restaurados desde el panel de administración del módulo RRHH.", false);
            comentario(t6, cli3, "Confirmo que ya puedo ingresar. Pueden cerrar el ticket.", false);
        }
    }

    private void comentario(Ticket ticket, Usuario autor, String mensaje, boolean interno) {
        ComentarioTicket c = new ComentarioTicket();
        c.setTicket(ticket);
        c.setAutor(autor);
        c.setMensaje(mensaje);
        c.setInterno(interno);
        comentarioRepo.save(c);
    }
}
