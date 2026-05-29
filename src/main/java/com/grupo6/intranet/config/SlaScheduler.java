package com.grupo6.intranet.config;

import com.grupo6.intranet.models.SlaConfig;
import com.grupo6.intranet.models.Ticket;
import com.grupo6.intranet.repositories.SlaConfigRepository;
import com.grupo6.intranet.repositories.TicketRepository;
import com.grupo6.intranet.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class SlaScheduler {

    private static final Logger log = LoggerFactory.getLogger(SlaScheduler.class);

    private static final double UMBRAL_ALERTA = 0.25;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private SlaConfigRepository slaConfigRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0/30 * * * *")
    public void revisarSlaTickets() {
        List<Ticket> tickets = ticketRepository.findAbiertosSinAlerta();
        if (tickets.isEmpty()) {
            log.debug("Scheduler SLA: sin tickets abiertos por revisar");
            return;
        }

        log.info("Scheduler SLA: revisando {} tickets abiertos", tickets.size());
        LocalDateTime ahora = LocalDateTime.now();
        int alertados = 0;

        for (Ticket ticket : tickets) {
            Optional<SlaConfig> slaOpt = slaConfigRepository.findByPrioridad(ticket.getPrioridad());
            if (slaOpt.isEmpty() || ticket.getCreatedAt() == null) continue;

            int horasResolucion = slaOpt.get().getTiempoResolucionHoras();
            long horasTranscurridas = Duration.between(ticket.getCreatedAt(), ahora).toHours();
            long horasRestantes = horasResolucion - horasTranscurridas;

            if (horasRestantes <= horasResolucion * UMBRAL_ALERTA && horasRestantes > 0) {
                emailService.notificarAlertaSla(ticket, (int) horasRestantes);
                ticket.setAlertaSlaEnviada(true);
                ticketRepository.save(ticket);
                alertados++;
                log.info("Alerta SLA enviada para ticket #{} ({} h restantes)", ticket.getId(), horasRestantes);
            } else if (horasRestantes <= 0) {
                emailService.notificarAlertaSla(ticket, 0);
                ticket.setAlertaSlaEnviada(true);
                ticketRepository.save(ticket);
                alertados++;
                log.warn("Ticket #{} vencio SLA (prioridad {})", ticket.getId(), ticket.getPrioridad());
            }
        }

        if (alertados > 0) {
            log.info("Scheduler SLA: {} alertas enviadas", alertados);
        }
    }
}
