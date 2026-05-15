package com.grupo6.intranet.repositories;

import com.grupo6.intranet.models.Estado;
import com.grupo6.intranet.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByClienteId(Long clienteId);
    List<Ticket> findByTecnicoId(Long tecnicoId);
    List<Ticket> findByEstado(Estado estado);
    List<Ticket> findByTecnicoIdAndEstadoNot(Long tecnicoId, Estado estado);
}
