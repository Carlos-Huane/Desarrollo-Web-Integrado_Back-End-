package com.grupo6.intranet.repositories;

import com.grupo6.intranet.models.HistorialTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialTicketRepository extends JpaRepository<HistorialTicket, Long> {
    List<HistorialTicket> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
