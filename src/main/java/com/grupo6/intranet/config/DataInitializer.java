package com.grupo6.intranet.config;

import com.grupo6.intranet.models.*;
import com.grupo6.intranet.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private CategoriaRepository categoriaRepo;
    @Autowired private SubcategoriaRepository subcategoriaRepo;
    @Autowired private TicketRepository ticketRepo;
    @Autowired private HistorialTicketRepository historialRepo;
    @Autowired private SlaConfigRepository slaRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepo.count() > 0) return;

        // ── USUARIOS (7 registros) ────────────────────────────
        Usuario admin1 = usuario("Carlos",  "Ramírez",  "admin@telecoperu.com",            "admin123",    "987654321", Rol.ADMIN);
        Usuario admin2 = usuario("Ana",     "Torres",   "gerente.ti@telecoperu.com",        "admin123",    "987654322", Rol.ADMIN);
        Usuario tec1   = usuario("Luis",    "Mendoza",  "tecnico1@telecoperu.com",          "tecnico123",  "987111001", Rol.TECNICO);
        Usuario tec2   = usuario("Rosa",    "Vega",     "tecnico2@telecoperu.com",          "tecnico123",  "987111002", Rol.TECNICO);
        Usuario cli1   = usuario("Pedro",   "Sánchez",  "pedro.sanchez@telecoperu.com",     "cliente123",  "987222001", Rol.CLIENTE);
        Usuario cli2   = usuario("María",   "López",    "maria.lopez@telecoperu.com",       "cliente123",  "987222002", Rol.CLIENTE);
        Usuario cli3   = usuario("José",    "Quispe",   "jose.quispe@telecoperu.com",       "cliente123",  "987222003", Rol.CLIENTE);

        // ── CATEGORÍAS (6 registros) ──────────────────────────
        Categoria red       = categoria("Red",              "Problemas de conectividad y redes");
        Categoria hardware  = categoria("Hardware",         "Fallas en equipos físicos");
        Categoria software  = categoria("Software",         "Errores en aplicaciones y sistemas");
        Categoria correo    = categoria("Correo",           "Incidencias con el correo corporativo");
        Categoria seguridad = categoria("Seguridad",        "Amenazas y vulnerabilidades de seguridad");
        Categoria acceso    = categoria("Acceso/Permisos",  "Problemas de acceso a sistemas");

        // ── SUBCATEGORÍAS (12 registros) ──────────────────────
        subcategoria("Conectividad lenta",      "Internet o red interna lenta",                 red);
        subcategoria("VPN sin acceso",           "No se puede conectar a la VPN",                red);
        subcategoria("PC no enciende",           "Equipo no inicia o no da señal",               hardware);
        subcategoria("Monitor con fallas",       "Pantalla con daño o sin imagen",               hardware);
        subcategoria("Error en aplicación",      "Aplicación interna falla al ejecutar",         software);
        subcategoria("Actualización fallida",    "Fallo durante la actualización del sistema",   software);
        subcategoria("No recibe correos",        "Bandeja de entrada sin nuevos mensajes",        correo);
        subcategoria("Correo bloqueado",         "Cuenta de correo bloqueada o suspendida",      correo);
        subcategoria("Virus detectado",          "Alerta de malware o virus en el equipo",       seguridad);
        subcategoria("Cuenta comprometida",      "Acceso no autorizado a cuenta de usuario",     seguridad);
        subcategoria("Sin acceso al sistema",    "Usuario no puede ingresar al sistema",         acceso);
        subcategoria("Permisos insuficientes",   "Usuario no tiene permisos para una función",   acceso);

        // ── SLA CONFIG (5 registros) ──────────────────────────
        sla(Prioridad.CRITICA,      1,  4,  "Interrupción total del servicio");
        sla(Prioridad.ALTA,         2,  8,  "Impacto alto en operaciones");
        sla(Prioridad.MEDIA,        4,  24, "Impacto moderado, existe workaround");
        sla(Prioridad.BAJA,         8,  72, "Impacto mínimo o cosmético");
        sla(Prioridad.SIN_ASIGNAR,  24, 96, "Pendiente de clasificación por técnico");

        // ── TICKETS (6 registros) ─────────────────────────────
        Ticket t1 = ticket(
                "Internet muy lento en sede Lima Norte",
                "La conexión a Internet en el piso 3 está muy lenta desde esta mañana.",
                cli1, tec1, red, Prioridad.ALTA, Estado.EN_ATENCION, false);

        Ticket t2 = ticket(
                "PC no enciende en área Contabilidad",
                "El equipo de la estación 12 no da señal al presionar el botón de encendido.",
                cli2, null, hardware, Prioridad.MEDIA, Estado.NUEVO, false);

        Ticket t3 = ticket(
                "Error al abrir el sistema de facturación",
                "Al intentar abrir FacturaSoft aparece error 500 y cierra la aplicación.",
                cli1, tec2, software, Prioridad.CRITICA, Estado.EN_ATENCION, false);

        Ticket t4 = ticket(
                "No recibo correos desde ayer",
                "Desde el miércoles no llegan correos a mi bandeja aunque mis compañeros sí reciben.",
                cli3, null, correo, Prioridad.MEDIA, Estado.NUEVO, false);

        Ticket t5 = ticket(
                "Alerta de virus en mi equipo",
                "El antivirus mostró alerta de troyano y bloqueó un archivo en Descargas.",
                cli2, tec2, seguridad, Prioridad.ALTA, Estado.RESUELTO, true);

        Ticket t6 = ticket(
                "Sin acceso al módulo de RRHH",
                "Desde la actualización del lunes no puedo ingresar al módulo de Recursos Humanos.",
                cli3, tec1, acceso, Prioridad.BAJA, Estado.CERRADO, true);

        // ── HISTORIAL TICKETS (13 registros) ──────────────────
        historial(t1, admin1, null,              Estado.NUEVO,        "Ticket creado por el cliente");
        historial(t1, admin1, Estado.NUEVO,      Estado.EN_ATENCION,  "Asignado a técnico Luis Mendoza");
        historial(t2, cli2,   null,              Estado.NUEVO,        "Ticket creado por el cliente");
        historial(t3, admin1, null,              Estado.NUEVO,        "Ticket creado por el cliente");
        historial(t3, admin1, Estado.NUEVO,      Estado.EN_ATENCION,  "Prioridad crítica, asignado de inmediato");
        historial(t4, cli3,   null,              Estado.NUEVO,        "Ticket creado por el cliente");
        historial(t5, admin1, null,              Estado.NUEVO,        "Ticket creado por el cliente");
        historial(t5, tec2,   Estado.NUEVO,      Estado.EN_ATENCION,  "Tomando el caso");
        historial(t5, tec2,   Estado.EN_ATENCION, Estado.RESUELTO,    "Virus eliminado con Malwarebytes, equipo escaneado limpio");
        historial(t6, admin1, null,              Estado.NUEVO,        "Ticket creado por el cliente");
        historial(t6, tec1,   Estado.NUEVO,      Estado.EN_ATENCION,  "Revisando permisos del módulo RRHH");
        historial(t6, tec1,   Estado.EN_ATENCION, Estado.RESUELTO,    "Permisos restaurados tras la actualización");
        historial(t6, cli3,   Estado.RESUELTO,   Estado.CERRADO,      "Cliente confirmó la resolución");
    }

    private Usuario usuario(String nombre, String apellido, String email, String pass, String tel, Rol rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(pass));
        u.setTelefono(tel);
        u.setRol(rol);
        u.setActivo(true);
        return usuarioRepo.save(u);
    }

    private Categoria categoria(String nombre, String descripcion) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setActivo(true);
        return categoriaRepo.save(c);
    }

    private void subcategoria(String nombre, String descripcion, Categoria categoria) {
        Subcategoria s = new Subcategoria();
        s.setNombre(nombre);
        s.setDescripcion(descripcion);
        s.setCategoria(categoria);
        s.setActivo(true);
        subcategoriaRepo.save(s);
    }

    private void sla(Prioridad prioridad, int respHoras, int resolHoras, String descripcion) {
        SlaConfig sla = new SlaConfig();
        sla.setPrioridad(prioridad);
        sla.setTiempoRespuestaHoras(respHoras);
        sla.setTiempoResolucionHoras(resolHoras);
        sla.setDescripcion(descripcion);
        slaRepo.save(sla);
    }

    private Ticket ticket(String titulo, String descripcion, Usuario cliente, Usuario tecnico,
                          Categoria categoria, Prioridad prioridad, Estado estado, boolean resuelto) {
        Ticket t = new Ticket();
        t.setTitulo(titulo);
        t.setDescripcion(descripcion);
        t.setCliente(cliente);
        t.setTecnico(tecnico);
        t.setCategoria(categoria);
        t.setPrioridad(prioridad);
        t.setEstado(estado);
        if (resuelto) {
            t.setFechaResolucion(LocalDateTime.now().minusDays(1));
        }
        return ticketRepo.save(t);
    }

    private void historial(Ticket ticket, Usuario usuario, Estado anterior, Estado nuevo, String comentario) {
        HistorialTicket h = new HistorialTicket();
        h.setTicket(ticket);
        h.setUsuario(usuario);
        h.setEstadoAnterior(anterior);
        h.setEstadoNuevo(nuevo);
        h.setComentario(comentario);
        historialRepo.save(h);
    }
}
