package com.grupo6.intranet.services;

import com.grupo6.intranet.models.Estado;
import com.grupo6.intranet.models.Ticket;
import com.grupo6.intranet.repositories.TicketRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private TicketRepository ticketRepository;

    public byte[] exportarTicketsExcel(Estado estado, LocalDateTime desde, LocalDateTime hasta) throws IOException {
        List<Ticket> tickets = ticketRepository.filtrar(estado, desde, hasta);
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Tickets");
            CellStyle headerStyle = estiloHeader(wb);

            String[] headers = {"ID", "Título", "Estado", "Prioridad", "Categoría",
                    "Cliente", "Técnico", "Creado", "Resuelto"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Ticket t : tickets) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getId());
                row.createCell(1).setCellValue(t.getTitulo());
                row.createCell(2).setCellValue(t.getEstado().name());
                row.createCell(3).setCellValue(t.getPrioridad().name());
                row.createCell(4).setCellValue(t.getCategoria() != null ? t.getCategoria().getNombre() : "");
                row.createCell(5).setCellValue(t.getCliente() != null ?
                        t.getCliente().getNombre() + " " + t.getCliente().getApellido() : "");
                row.createCell(6).setCellValue(t.getTecnico() != null ?
                        t.getTecnico().getNombre() + " " + t.getTecnico().getApellido() : "Sin asignar");
                row.createCell(7).setCellValue(t.getCreatedAt() != null ? t.getCreatedAt().format(FMT) : "");
                row.createCell(8).setCellValue(t.getFechaResolucion() != null ?
                        t.getFechaResolucion().format(FMT) : "");
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportarTicketsPdf(Estado estado, LocalDateTime desde, LocalDateTime hasta) {
        List<Ticket> tickets = ticketRepository.filtrar(estado, desde, hasta);
        Document doc = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
        Paragraph titulo = new Paragraph("Reporte de Tickets — Intranet TelecoPerú", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Paragraph sub = new Paragraph("Generado: " + LocalDateTime.now().format(FMT) +
                "   |   Total: " + tickets.size() + " tickets", subFont);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(15);
        doc.add(sub);

        PdfPTable table = new PdfPTable(new float[]{0.6f, 3f, 1.2f, 1.2f, 1.5f, 1.8f, 1.8f, 1.5f});
        table.setWidthPercentage(100);

        String[] headers = {"ID", "Título", "Estado", "Prioridad", "Categoría", "Cliente", "Técnico", "Creado"};
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(33, 79, 153));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        for (Ticket t : tickets) {
            table.addCell(celda(String.valueOf(t.getId()), cellFont));
            table.addCell(celda(t.getTitulo(), cellFont));
            table.addCell(celda(t.getEstado().name(), cellFont));
            table.addCell(celda(t.getPrioridad().name(), cellFont));
            table.addCell(celda(t.getCategoria() != null ? t.getCategoria().getNombre() : "", cellFont));
            table.addCell(celda(t.getCliente() != null ?
                    t.getCliente().getNombre() + " " + t.getCliente().getApellido() : "", cellFont));
            table.addCell(celda(t.getTecnico() != null ?
                    t.getTecnico().getNombre() + " " + t.getTecnico().getApellido() : "Sin asignar", cellFont));
            table.addCell(celda(t.getCreatedAt() != null ? t.getCreatedAt().format(FMT) : "", cellFont));
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private PdfPCell celda(String texto, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(texto, font));
        c.setPadding(4);
        return c;
    }

    private CellStyle estiloHeader(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
