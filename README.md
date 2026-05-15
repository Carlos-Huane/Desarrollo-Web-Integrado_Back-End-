# Intranet de Gestión de Incidencias — TelecoPerú S.A.C.
**Curso:** Desarrollo Web Integrado · Grupo 6  
**Docente:** Marcelino Estrada Aro

---

## Stack técnico
- Java 21 + Spring Boot 3.3.5
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL 8
- Maven

---

## Prerequisitos

Instalar lo siguiente antes de clonar:

| Herramienta | Versión mínima | Descarga |
|---|---|---|
| JDK | 21 | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org/download |
| MySQL | 8.0+ | https://dev.mysql.com/downloads/mysql |
| DBeaver | cualquiera | https://dbeaver.io/download |
| VS Code | cualquiera | https://code.visualstudio.com |
| Extensión Spring Boot Dashboard | — | Buscar en VS Code Extensions: `vscjava.vscode-spring-boot-dashboard` |

---

## 1. Clonar el repositorio

```bash
git clone https://github.com/Carlos-Huane/Desarrollo-Web-Integrado_Back-End-.git
cd Desarrollo-Web-Integrado_Back-End-
```

---

## 2. Crear la base de datos en DBeaver

### 2.1 Conectar MySQL

1. Abrir DBeaver
2. Click en el ícono de enchufe → **New Database Connection**
3. Seleccionar **MySQL** → Next
4. Completar:
   - Host: `localhost`
   - Port: `3306`
   - Username: `root`
   - Password: *(la contraseña de tu MySQL local)*
5. En el tab **Driver properties** agregar:
   - `allowPublicKeyRetrieval` = `true`
   - `useSSL` = `false`
6. Click **Test Connection** → debe decir "Connected"
7. Click **Finish**

### 2.2 Crear la base de datos

En DBeaver, click derecho sobre la conexión → **SQL Editor → Open SQL Script** y ejecutar:

```sql
CREATE DATABASE intranet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Presionar `Ctrl + Enter`. Verificar que aparece `intranet_db` en el panel izquierdo (click derecho → Refresh si no aparece).

---

## 3. Configurar el proyecto

Abrir `src/main/resources/application.properties` y editar:

```properties
spring.datasource.password=TU_PASSWORD_AQUI
```

Reemplazar `TU_PASSWORD_AQUI` con tu contraseña de MySQL. Si no tienes, dejar en blanco.

---

## 4. Levantar el servidor

### Opción A — VS Code con Spring Boot Dashboard (recomendado)

1. Abrir VS Code → **File → Open Folder** → seleccionar la carpeta del proyecto
2. Esperar que Maven descargue las dependencias
3. En el panel izquierdo abrir **Spring Boot Dashboard**
4. Expandir **APPS** → click en ▶ junto a `intranet`
5. Cuando aparezca `Started IntranetApplication` en el log, el servidor está listo

### Opción B — Terminal

```bash
mvn spring-boot:run
```

### Verificar

Servidor en: `http://localhost:8080`

Al levantar por primera vez Hibernate crea las tablas e inserta los datos automáticamente. Verificar en DBeaver con **Refresh** en `intranet_db`.

---

## 5. Probar con Postman

### Login (sin token)

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@telecoperu.com",
  "password": "admin123"
}
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@telecoperu.com",
  "rol": "ADMIN",
  "nombreCompleto": "Carlos Ramírez"
}
```

Usar el token en las demás peticiones:
```
Authorization: Bearer <token>
```

### Sin token (público)

```
GET http://localhost:8080/api/categorias
```

---

## 6. Credenciales de prueba

| Email | Password | Rol |
|---|---|---|
| admin@telecoperu.com | admin123 | ADMIN |
| gerente.ti@telecoperu.com | admin123 | ADMIN |
| tecnico1@telecoperu.com | tecnico123 | TECNICO |
| tecnico2@telecoperu.com | tecnico123 | TECNICO |
| pedro.sanchez@telecoperu.com | cliente123 | CLIENTE |
| maria.lopez@telecoperu.com | cliente123 | CLIENTE |
| jose.quispe@telecoperu.com | cliente123 | CLIENTE |

---

## 7. Endpoints disponibles

### Auth
| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/auth/login` | No | Iniciar sesión |

### Usuarios
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Listar todos |
| GET | `/api/usuarios/activos` | Listar activos |
| GET | `/api/usuarios/tecnicos` | Listar técnicos |
| GET | `/api/usuarios/{id}` | Buscar por ID |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Actualizar |
| PATCH | `/api/usuarios/{id}/activar` | Activar cuenta |
| PATCH | `/api/usuarios/{id}/desactivar` | Desactivar cuenta |

### Categorías
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/categorias` | Listar activas (sin token) |
| GET | `/api/categorias/todas` | Listar todas |
| POST | `/api/categorias` | Crear |
| PUT | `/api/categorias/{id}` | Actualizar |
| PATCH | `/api/categorias/{id}/activar` | Activar |
| PATCH | `/api/categorias/{id}/desactivar` | Desactivar |
| GET | `/api/categorias/{id}/subcategorias` | Ver subcategorías |
| POST | `/api/categorias/subcategorias` | Crear subcategoría |

### Tickets
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/tickets` | Listar todos |
| GET | `/api/tickets/{id}` | Buscar por ID |
| GET | `/api/tickets/cliente/{clienteId}` | Tickets de un cliente |
| GET | `/api/tickets/tecnico/{tecnicoId}` | Bandeja del técnico |
| POST | `/api/tickets/cliente/{clienteId}` | Crear ticket |
| PATCH | `/api/tickets/{ticketId}/estado/{usuarioId}` | Cambiar estado |
| GET | `/api/tickets/{ticketId}/historial` | Ver historial |

### SLA
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/sla` | Ver configuración |
| PUT | `/api/sla/{id}` | Actualizar tiempos |

---

## 8. Estructura del proyecto

```
src/main/java/com/grupo6/intranet/
├── config/
│   ├── DataInitializer.java   # Datos de prueba al primer arranque
│   ├── JwtFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
├── controllers/
│   ├── AuthController.java
│   ├── CategoriaController.java
│   ├── SlaConfigController.java
│   ├── TicketController.java
│   └── UsuarioController.java
├── dtos/
│   ├── CambioEstadoRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── TicketRequest.java
├── models/
│   ├── Categoria.java
│   ├── Estado.java            # NUEVO, EN_ATENCION, ESCALADO, RESUELTO, CERRADO
│   ├── HistorialTicket.java
│   ├── Prioridad.java         # CRITICA, ALTA, MEDIA, BAJA, SIN_ASIGNAR
│   ├── Rol.java               # ADMIN, TECNICO, CLIENTE
│   ├── SlaConfig.java
│   ├── Subcategoria.java
│   ├── Ticket.java
│   └── Usuario.java
├── repositories/
│   └── ...
├── services/
│   └── ...
└── IntranetApplication.java
```

---

## 9. Tablas en MySQL

| Tabla | Registros iniciales |
|---|---|
| `usuarios` | 7 |
| `categorias` | 6 |
| `subcategorias` | 12 |
| `tickets` | 6 |
| `historial_tickets` | 13 |
| `sla_config` | 5 |

---

## Integrantes

| Nombre | Rol Scrum |
|---|---|
| Rodriguez Pozo, Matias Ariel | Product Owner |
| Huane Sarmiento, Carlos Jesus | Scrum Master |
| Gonzales Alvis, Claudia Leonor | Development Team |
| Prado Misaico, Bartolome Angelo | Development Team |
| Rodriguez Chacaliaza, Airton Clides | Development Team |
