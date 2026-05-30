package com.grupo6.intranet.services;

import com.grupo6.intranet.models.Estado;
import com.grupo6.intranet.models.Ticket;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${intranet.mail.from:intranet@telecoperu.com}")
    private String from;

    @Value("${intranet.mail.enabled:false}")
    private boolean enabled;

    public void notificarTicketCreado(Ticket ticket) {
        if (!habilitado() || ticket.getCliente() == null) return;
        String asunto = "[Intranet] Ticket #" + ticket.getId() + " registrado";
        String cuerpo = """
                <h2>Tu ticket fue registrado</h2>
                <p>Hola %s,</p>
                <p>Hemos recibido tu solicitud y un técnico la atenderá pronto.</p>
                <table style="border-collapse:collapse;">
                  <tr><td><b>Ticket:</b></td><td>#%d</td></tr>
                  <tr><td><b>Título:</b></td><td>%s</td></tr>
                  <tr><td><b>Categoría:</b></td><td>%s</td></tr>
                  <tr><td><b>Prioridad:</b></td><td>%s</td></tr>
                  <tr><td><b>Fecha:</b></td><td>%s</td></tr>
                </table>
                <p style="color:#666; font-size:12px; margin-top:20px;">— Intranet TelecoPerú</p>
                """.formatted(
                ticket.getCliente().getNombre(),
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getCategoria() != null ? ticket.getCategoria().getNombre() : "—",
                ticket.getPrioridad().name(),
                ticket.getCreatedAt() != null ? ticket.getCreatedAt().format(FMT) : "");
        enviar(ticket.getCliente().getEmail(), asunto, cuerpo);
    }

    public void notificarAsignacionTecnico(Ticket ticket) {
        if (!habilitado() || ticket.getTecnico() == null) return;
        String asunto = "[Intranet] Ticket #" + ticket.getId() + " asignado a ti";
        String cuerpo = """
                <h2>Nuevo ticket asignado</h2>
                <p>Hola %s,</p>
                <p>Se te asignó el siguiente ticket. Por favor revísalo cuanto antes.</p>
                <table style="border-collapse:collapse;">
                  <tr><td><b>Ticket:</b></td><td>#%d</td></tr>
                  <tr><td><b>Título:</b></td><td>%s</td></tr>
                  <tr><td><b>Cliente:</b></td><td>%s %s</td></tr>
                  <tr><td><b>Prioridad:</b></td><td>%s</td></tr>
                  <tr><td><b>Descripción:</b></td><td>%s</td></tr>
                </table>
                <p style="color:#666; font-size:12px; margin-top:20px;">— Intranet TelecoPerú</p>
                """.formatted(
                ticket.getTecnico().getNombre(),
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getCliente() != null ? ticket.getCliente().getNombre() : "",
                ticket.getCliente() != null ? ticket.getCliente().getApellido() : "",
                ticket.getPrioridad().name(),
                ticket.getDescripcion());
        enviar(ticket.getTecnico().getEmail(), asunto, cuerpo);
    }

    public void notificarCambioEstado(Ticket ticket, Estado anterior, Estado nuevo, String comentario) {
        if (!habilitado() || ticket.getCliente() == null) return;
        String asunto = "[Intranet] Ticket #" + ticket.getId() + " — " + nuevo.name();
        String cuerpo = """
                <h2>Tu ticket cambió de estado</h2>
                <p>Hola %s,</p>
                <table style="border-collapse:collapse;">
                  <tr><td><b>Ticket:</b></td><td>#%d - %s</td></tr>
                  <tr><td><b>Estado anterior:</b></td><td>%s</td></tr>
                  <tr><td><b>Estado nuevo:</b></td><td><b>%s</b></td></tr>
                  <tr><td><b>Comentario:</b></td><td>%s</td></tr>
                </table>
                <p style="color:#666; font-size:12px; margin-top:20px;">— Intranet TelecoPerú</p>
                """.formatted(
                ticket.getCliente().getNombre(),
                ticket.getId(),
                ticket.getTitulo(),
                anterior != null ? anterior.name() : "—",
                nuevo.name(),
                comentario != null ? comentario : "Sin comentarios");
        enviar(ticket.getCliente().getEmail(), asunto, cuerpo);
    }

    public void notificarAlertaSla(Ticket ticket, int horasRestantes) {
        if (!habilitado()) return;
        String destinatario = ticket.getTecnico() != null
                ? ticket.getTecnico().getEmail()
                : (ticket.getCliente() != null ? ticket.getCliente().getEmail() : null);
        if (destinatario == null) return;
        String asunto = "[ALERTA SLA] Ticket #" + ticket.getId() + " por vencer";
        String cuerpo = """
                <h2 style="color:#c0392b;">Alerta de SLA</h2>
                <p>El siguiente ticket está por vencer su tiempo de resolución.</p>
                <table style="border-collapse:collapse;">
                  <tr><td><b>Ticket:</b></td><td>#%d - %s</td></tr>
                  <tr><td><b>Prioridad:</b></td><td>%s</td></tr>
                  <tr><td><b>Estado:</b></td><td>%s</td></tr>
                  <tr><td><b>Horas restantes:</b></td><td><b>%d h</b></td></tr>
                </table>
                <p style="color:#666; font-size:12px; margin-top:20px;">— Intranet TelecoPerú</p>
                """.formatted(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getPrioridad().name(),
                ticket.getEstado().name(),
                horasRestantes);
        enviar(destinatario, asunto, cuerpo);
    }

    private boolean habilitado() {
        if (!enabled) {
            log.debug("Notificaciones por correo deshabilitadas (intranet.mail.enabled=false)");
            return false;
        }
        if (mailSender == null) {
            log.warn("JavaMailSender no disponible — revisar configuración SMTP");
            return false;
        }
        return true;
    }

    private void enviar(String destinatario, String asunto, String cuerpoHtml) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpoHtml, true);
            mailSender.send(msg);
            log.info("Correo enviado a {} — asunto: {}", destinatario, asunto);
        } catch (MessagingException e) {
            log.error("Error armando correo para {}: {}", destinatario, e.getMessage());
        } catch (Exception e) {
            log.error("Fallo enviando correo a {}: {}", destinatario, e.getMessage());
        }
    }
}
