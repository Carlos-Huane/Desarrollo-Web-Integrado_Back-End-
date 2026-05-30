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

    @Query("SELECT t FROM Ticket t WHERE t.estado IN (com.grupo6.intranet.models.Estado.NUEVO, " +
           "com.grupo6.intranet.models.Estado.EN_ATENCION, com.grupo6.intranet.models.Estado.ESCALADO) " +
           "AND t.alertaSlaEnviada = false")
    List<Ticket> findAbiertosSinAlerta();

    long countByEstado(Estado estado);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:estado IS NULL OR t.estado = :estado) AND " +
           "(:desde IS NULL OR t.createdAt >= :desde) AND " +
           "(:hasta IS NULL OR t.createdAt <= :hasta) " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> filtrar(@org.springframework.data.repository.query.Param("estado") Estado estado,
                         @org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde,
                         @org.springframework.data.repository.query.Param("hasta") java.time.LocalDateTime hasta);

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

    @Query(value = "SELECT AVG(TIMESTAMPDIFF(HOUR, t.created_at, t.fecha_resolucion)) " +
           "FROM tickets t WHERE t.fecha_resolucion IS NOT NULL", nativeQuery = true)
    Number tiempoPromedioResolucionHorasRaw();

    default Double tiempoPromedioResolucionHoras() {
        Number n = tiempoPromedioResolucionHorasRaw();
        return n != null ? n.doubleValue() : 0.0;
    }

    @Query(value = "SELECT c.id, c.nombre, COUNT(t.id), " +
           "AVG(TIMESTAMPDIFF(HOUR, t.created_at, t.fecha_resolucion)) " +
           "FROM tickets t JOIN categorias c ON c.id = t.categoria_id " +
           "WHERE t.fecha_resolucion IS NOT NULL " +
           "GROUP BY c.id, c.nombre " +
           "ORDER BY COUNT(t.id) DESC", nativeQuery = true)
    List<Object[]> rankingCategoriasConTiempo();

    @Query(value = "SELECT u.id, u.nombre, u.apellido, COUNT(t.id), " +
           "AVG(TIMESTAMPDIFF(HOUR, t.created_at, t.fecha_resolucion)) " +
           "FROM tickets t JOIN usuarios u ON u.id = t.tecnico_id " +
           "WHERE t.tecnico_id IS NOT NULL AND t.fecha_resolucion IS NOT NULL " +
           "GROUP BY u.id, u.nombre, u.apellido " +
           "ORDER BY COUNT(t.id) DESC", nativeQuery = true)
    List<Object[]> rankingTecnicosConTiempo();
}
