"""
Genera el diagrama entidad-relacion de la BD intranet_db como PNG y JPG.
Salida: docs/diagrama-bd.png y docs/diagrama-bd.jpg
"""
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.patches import FancyArrowPatch

TABLAS = {
    "usuarios": {
        "pos": (1.0, 7.5),
        "size": (3.2, 3.0),
        "color": "#FFE4B5",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("nombre", "VARCHAR", ""),
            ("apellido", "VARCHAR", ""),
            ("email", "VARCHAR", "UQ"),
            ("password", "VARCHAR", ""),
            ("telefono", "VARCHAR", ""),
            ("rol", "ENUM", ""),
            ("activo", "BOOLEAN", ""),
            ("created_at", "DATETIME", ""),
        ],
    },
    "categorias": {
        "pos": (10.5, 8.5),
        "size": (3.0, 1.7),
        "color": "#C6E2FF",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("nombre", "VARCHAR", "UQ"),
            ("descripcion", "VARCHAR", ""),
            ("activo", "BOOLEAN", ""),
        ],
    },
    "subcategorias": {
        "pos": (10.5, 5.5),
        "size": (3.0, 1.9),
        "color": "#C6E2FF",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("nombre", "VARCHAR", ""),
            ("descripcion", "VARCHAR", ""),
            ("activo", "BOOLEAN", ""),
            ("categoria_id", "BIGINT", "FK"),
        ],
    },
    "tickets": {
        "pos": (5.5, 5.0),
        "size": (3.7, 4.2),
        "color": "#FFB6C1",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("titulo", "VARCHAR", ""),
            ("descripcion", "TEXT", ""),
            ("prioridad", "ENUM", ""),
            ("estado", "ENUM", ""),
            ("created_at", "DATETIME", ""),
            ("updated_at", "DATETIME", ""),
            ("fecha_resolucion", "DATETIME", ""),
            ("alerta_sla_enviada", "BOOLEAN", ""),
            ("cliente_id", "BIGINT", "FK"),
            ("tecnico_id", "BIGINT", "FK"),
            ("categoria_id", "BIGINT", "FK"),
            ("subcategoria_id", "BIGINT", "FK"),
        ],
    },
    "historial_tickets": {
        "pos": (5.5, 0.5),
        "size": (3.7, 2.4),
        "color": "#FFE4E1",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("estado_anterior", "ENUM", ""),
            ("estado_nuevo", "ENUM", ""),
            ("comentario", "VARCHAR", ""),
            ("created_at", "DATETIME", ""),
            ("ticket_id", "BIGINT", "FK"),
            ("usuario_id", "BIGINT", "FK"),
        ],
    },
    "sla_config": {
        "pos": (10.5, 2.5),
        "size": (3.2, 1.9),
        "color": "#D8BFD8",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("prioridad", "ENUM", "UQ"),
            ("tiempo_respuesta_horas", "INT", ""),
            ("tiempo_resolucion_horas", "INT", ""),
            ("descripcion", "VARCHAR", ""),
        ],
    },
    "articulos": {
        "pos": (1.0, 2.5),
        "size": (3.5, 3.0),
        "color": "#C7F0BD",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("titulo", "VARCHAR", ""),
            ("contenido", "TEXT", ""),
            ("vistas", "BIGINT", ""),
            ("activo", "BOOLEAN", ""),
            ("created_at", "DATETIME", ""),
            ("updated_at", "DATETIME", ""),
            ("categoria_id", "BIGINT", "FK"),
            ("autor_id", "BIGINT", "FK"),
        ],
    },
    "comentarios_ticket": {
        "pos": (9.6, 0.2),
        "size": (4.5, 2.2),
        "color": "#FFDAB9",
        "cols": [
            ("id", "BIGINT", "PK"),
            ("mensaje", "TEXT", ""),
            ("interno", "BOOLEAN", ""),
            ("created_at", "DATETIME", ""),
            ("ticket_id", "BIGINT", "FK"),
            ("autor_id", "BIGINT", "FK"),
        ],
    },
}

RELACIONES = [
    ("subcategorias", "categoria_id", "categorias"),
    ("tickets", "cliente_id", "usuarios"),
    ("tickets", "tecnico_id", "usuarios"),
    ("tickets", "categoria_id", "categorias"),
    ("tickets", "subcategoria_id", "subcategorias"),
    ("historial_tickets", "ticket_id", "tickets"),
    ("historial_tickets", "usuario_id", "usuarios"),
    ("articulos", "categoria_id", "categorias"),
    ("articulos", "autor_id", "usuarios"),
    ("comentarios_ticket", "ticket_id", "tickets"),
    ("comentarios_ticket", "autor_id", "usuarios"),
]

# Relaciones lógicas (no FK formal, pero los datos se cruzan por columna)
RELACIONES_LOGICAS = [
    ("sla_config", "prioridad", "tickets", "prioridad"),
]


def dibujar_tabla(ax, nombre, info):
    x, y = info["pos"]
    w, h = info["size"]
    color = info["color"]
    cols = info["cols"]

    titulo_h = 0.45
    fila_h = (h - titulo_h) / len(cols)

    ax.add_patch(patches.FancyBboxPatch(
        (x, y), w, h, boxstyle="round,pad=0.02,rounding_size=0.05",
        linewidth=1.8, edgecolor="black", facecolor=color, zorder=2))

    ax.add_patch(patches.Rectangle(
        (x, y + h - titulo_h), w, titulo_h,
        linewidth=0, facecolor="#333333", zorder=3))
    ax.text(x + w / 2, y + h - titulo_h / 2, nombre,
            ha="center", va="center", fontsize=11, fontweight="bold",
            color="white", zorder=4)

    for i, (col_nombre, col_tipo, badge) in enumerate(cols):
        fy = y + h - titulo_h - (i + 1) * fila_h
        if i < len(cols) - 1:
            ax.plot([x, x + w], [fy, fy], color="#aaaaaa", linewidth=0.4, zorder=3)
        prefijo = f"[{badge}] " if badge else "    "
        ax.text(x + 0.1, fy + fila_h / 2, f"{prefijo}{col_nombre}",
                ha="left", va="center", fontsize=8, fontfamily="monospace", zorder=4)
        ax.text(x + w - 0.1, fy + fila_h / 2, col_tipo,
                ha="right", va="center", fontsize=7, color="#555", style="italic", zorder=4)


def centro_columna(info, col_nombre):
    x, y = info["pos"]
    w, h = info["size"]
    cols = info["cols"]
    titulo_h = 0.45
    fila_h = (h - titulo_h) / len(cols)
    for i, (n, _, _) in enumerate(cols):
        if n == col_nombre:
            fy = y + h - titulo_h - (i + 1) * fila_h + fila_h / 2
            return x, x + w, fy
    return x, x + w, y + h / 2


def dibujar_relacion(ax, origen, col_fk, destino):
    info_o = TABLAS[origen]
    info_d = TABLAS[destino]
    ox_l, ox_r, oy = centro_columna(info_o, col_fk)
    dx_l, dx_r, dy = centro_columna(info_d, "id")

    centro_o = (info_o["pos"][0] + info_o["size"][0] / 2,
                info_o["pos"][1] + info_o["size"][1] / 2)
    centro_d = (info_d["pos"][0] + info_d["size"][0] / 2,
                info_d["pos"][1] + info_d["size"][1] / 2)

    if centro_d[0] > centro_o[0]:
        x_o = ox_r
        x_d = dx_l
    else:
        x_o = ox_l
        x_d = dx_r

    arrow = FancyArrowPatch(
        (x_o, oy), (x_d, dy),
        connectionstyle="arc3,rad=0.15",
        arrowstyle="-|>", mutation_scale=12,
        linewidth=1.3, color="#0066cc", zorder=1)
    ax.add_patch(arrow)


def dibujar_relacion_logica(ax, origen, col_origen, destino, col_destino):
    info_o = TABLAS[origen]
    info_d = TABLAS[destino]
    ox_l, ox_r, oy = centro_columna(info_o, col_origen)
    dx_l, dx_r, dy = centro_columna(info_d, col_destino)

    centro_o = (info_o["pos"][0] + info_o["size"][0] / 2,
                info_o["pos"][1] + info_o["size"][1] / 2)
    centro_d = (info_d["pos"][0] + info_d["size"][0] / 2,
                info_d["pos"][1] + info_d["size"][1] / 2)

    if centro_d[0] > centro_o[0]:
        x_o = ox_r
        x_d = dx_l
    else:
        x_o = ox_l
        x_d = dx_r

    arrow = FancyArrowPatch(
        (x_o, oy), (x_d, dy),
        connectionstyle="arc3,rad=-0.20",
        arrowstyle="-", mutation_scale=10,
        linewidth=1.2, color="#888888", linestyle="dashed", zorder=1)
    ax.add_patch(arrow)

    mid_x = (x_o + x_d) / 2
    mid_y = (oy + dy) / 2 + 0.25
    ax.text(mid_x, mid_y, "lógica",
            ha="center", va="center", fontsize=7,
            color="#666666", style="italic",
            bbox=dict(boxstyle="round,pad=0.15", facecolor="white",
                      edgecolor="#cccccc", linewidth=0.6), zorder=2)


def main():
    fig, ax = plt.subplots(figsize=(16, 11))
    ax.set_xlim(0, 14.5)
    ax.set_ylim(-0.5, 11)
    ax.set_aspect("equal")
    ax.axis("off")

    plt.title("Modelo de Datos — Intranet TelecoPerú (intranet_db)",
              fontsize=16, fontweight="bold", pad=20)

    for nombre, info in TABLAS.items():
        dibujar_tabla(ax, nombre, info)

    for origen, col_fk, destino in RELACIONES:
        dibujar_relacion(ax, origen, col_fk, destino)

    for origen, col_o, destino, col_d in RELACIONES_LOGICAS:
        dibujar_relacion_logica(ax, origen, col_o, destino, col_d)

    leyenda_x = 0.3
    leyenda_y = 0.0
    ax.text(leyenda_x, leyenda_y,
            "Leyenda:  [PK] Primary Key   [FK] Foreign Key   [UQ] Unique   "
            "—► relación FK   ╌╌ relación lógica (sin FK formal)",
            fontsize=9, fontfamily="monospace", color="#333")

    plt.tight_layout()
    plt.savefig("docs/diagrama-bd.png", dpi=150, bbox_inches="tight", facecolor="white")
    plt.savefig("docs/diagrama-bd.jpg", dpi=150, bbox_inches="tight", facecolor="white")
    print("Generado: docs/diagrama-bd.png")
    print("Generado: docs/diagrama-bd.jpg")


if __name__ == "__main__":
    main()
