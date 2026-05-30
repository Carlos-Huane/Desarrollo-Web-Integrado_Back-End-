package com.grupo6.intranet.repositories;

import com.grupo6.intranet.models.ComentarioTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioTicketRepository extends JpaRepository<ComentarioTicket, Long> {
    List<ComentarioTicket> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
    List<ComentarioTicket> findByTicketIdAndInternoFalseOrderByCreatedAtAsc(Long ticketId);
    long countByTicketId(Long ticketId);
}
