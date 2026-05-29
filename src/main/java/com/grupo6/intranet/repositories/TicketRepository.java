package com.grupo6.intranet.repositories;

import com.grupo6.intranet.models.Estado;
import com.grupo6.intranet.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByClienteId(Long clienteId);
    List<Ticket> findByTecnicoId(Long tecnicoId);
    List<Ticket> findByEstado(Estado estado);
    List<Ticket> findByTecnicoIdAndEstadoNot(Long tecnicoId, Estado estado);

    long countByEstado(Estado estado);

    @Query("SELECT t.estado, COUNT(t) FROM Ticket t GROUP BY t.estado")
    List<Object[]> contarPorEstado();

    @Query("SELECT t.prioridad, COUNT(t) FROM Ticket t GROUP BY t.prioridad")
    List<Object[]> contarPorPrioridad();

    @Query("SELECT t.categoria.nombre, COUNT(t) FROM Ticket t GROUP BY t.categoria.nombre ORDER BY COUNT(t) DESC")
    List<Object[]> contarPorCategoria();

    @Query("SELECT t.tecnico.id, t.tecnico.nombre, t.tecnico.apellido, COUNT(t) " +
           "FROM Ticket t WHERE t.tecnico IS NOT NULL " +
           "GROUP BY t.tecnico.id, t.tecnico.nombre, t.tecnico.apellido " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> contarPorTecnico();

    @Query("SELECT t.tecnico.id, t.tecnico.nombre, t.tecnico.apellido, COUNT(t) " +
           "FROM Ticket t WHERE t.tecnico IS NOT NULL AND t.estado = com.grupo6.intranet.models.Estado.RESUELTO " +
           "GROUP BY t.tecnico.id, t.tecnico.nombre, t.tecnico.apellido " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> contarResueltosPorTecnico();

    @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', HOUR, t.createdAt, t.fechaResolucion)) " +
           "FROM Ticket t WHERE t.fechaResolucion IS NOT NULL")
    Double tiempoPromedioResolucionHoras();

    @Query("SELECT t.categoria.id, t.categoria.nombre, COUNT(t), " +
           "AVG(FUNCTION('TIMESTAMPDIFF', HOUR, t.createdAt, t.fechaResolucion)) " +
           "FROM Ticket t WHERE t.fechaResolucion IS NOT NULL " +
           "GROUP BY t.categoria.id, t.categoria.nombre " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> rankingCategoriasConTiempo();

    @Query("SELECT t.tecnico.id, t.tecnico.nombre, t.tecnico.apellido, COUNT(t), " +
           "AVG(FUNCTION('TIMESTAMPDIFF', HOUR, t.createdAt, t.fechaResolucion)) " +
           "FROM Ticket t WHERE t.tecnico IS NOT NULL AND t.fechaResolucion IS NOT NULL " +
           "GROUP BY t.tecnico.id, t.tecnico.nombre, t.tecnico.apellido " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> rankingTecnicosConTiempo();
}
