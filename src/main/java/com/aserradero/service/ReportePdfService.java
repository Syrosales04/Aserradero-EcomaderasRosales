package com.aserradero.service;

import com.aserradero.dto.ReporteSemanalDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Genera el reporte semanal de ECOMADERAS ROSALES en formato PDF,
 * usando los mismos datos y la misma logica que ReporteService.
 */
@Service
public class ReportePdfService {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Font TITULO = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(22, 58, 40));
    private static final Font SUBTITULO = new Font(Font.HELVETICA, 11, Font.NORMAL, new Color(112, 70, 38));
    private static final Font SECCION = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(22, 58, 40));
    private static final Font ETIQUETA = new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY);
    private static final Font VALOR = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
    private static final Font PIE = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);

    public byte[] generarReporteSemanal(ReporteSemanalDTO r) {
        Document documento = new Document(PageSize.A4, 42, 42, 40, 40);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            agregarEncabezado(documento, r);
            agregarSeccionProduccion(documento, r);
            agregarSeccionFinanciera(documento, r);
            agregarSeccionConteos(documento, r);
            agregarPie(documento);

            documento.close();
        } catch (DocumentException e) {
            throw new RuntimeException("No se pudo generar el PDF del reporte: " + e.getMessage(), e);
        }

        return salida.toByteArray();
    }

    private void agregarEncabezado(Document documento, ReporteSemanalDTO r) throws DocumentException {
        Paragraph nombre = new Paragraph("ECOMADERAS ROSALES", TITULO);
        nombre.setAlignment(Element.ALIGN_CENTER);
        documento.add(nombre);

        Paragraph subtitulo = new Paragraph("Reporte semanal de rendimiento y ganancia", SUBTITULO);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(4);
        documento.add(subtitulo);

        String periodo = "Periodo del reporte: " + formatear(r.getDesde()) + " al " + formatear(r.getHasta())
                + "  (" + diaSemana(r.getDesde()) + " a " + diaSemana(r.getHasta()) + ")";
        Paragraph periodoP = new Paragraph(periodo, VALOR);
        periodoP.setAlignment(Element.ALIGN_CENTER);
        periodoP.setSpacingAfter(14);
        documento.add(periodoP);

        LineSeparator linea = new LineSeparator();
        linea.setLineColor(new Color(185, 118, 42));
        documento.add(new Chunk(linea));
        documento.add(Chunk.NEWLINE);
    }

    private void agregarSeccionProduccion(Document documento, ReporteSemanalDTO r) throws DocumentException {
        documento.add(new Paragraph("Rendimiento de madera", SECCION));
        documento.add(Chunk.NEWLINE);

        PdfPTable tabla = nuevaTabla();
        fila(tabla, "Madera ingresada", num(r.getMaderaIngresada()) + " pulgadas");
        fila(tabla, "Madera procesada", num(r.getMaderaProcesada()) + " pulgadas");
        fila(tabla, "Aprovechamiento real", num(r.getAprovechamientoReal()) + " %");
        fila(tabla, "Desperdicio real", num(r.getDesperdicioReal()) + " pulgadas (" + num(r.getDesperdicioRealPorcentaje()) + " %)");
        fila(tabla, "Estandar esperado (referencia)", num(r.getEstandarEsperado()) + " % ("
                + num(r.getMaderaAprovechableEstandar()) + " pulgadas aprovechables segun el estandar)");
        documento.add(tabla);
        documento.add(Chunk.NEWLINE);
    }

    private void agregarSeccionFinanciera(Document documento, ReporteSemanalDTO r) throws DocumentException {
        documento.add(new Paragraph("Resultado financiero", SECCION));
        documento.add(Chunk.NEWLINE);

        PdfPTable tabla = nuevaTabla();
        fila(tabla, "Compras realizadas", moneda(r.getTotalCompras()));
        fila(tabla, "Ventas realizadas", moneda(r.getTotalVentas()));
        fila(tabla, "Ganancia actual (ventas - compras)", moneda(r.getGananciaActual()));
        fila(tabla, "Valor del inventario restante", moneda(r.getValorInventarioRestante()));
        fila(tabla, "Ganancia estimada final", moneda(r.getGananciaEstimada()));
        fila(tabla, "Pendiente por recuperar", moneda(r.getPendienteRecuperar()));
        documento.add(tabla);
        documento.add(Chunk.NEWLINE);
    }

    private void agregarSeccionConteos(Document documento, ReporteSemanalDTO r) throws DocumentException {
        documento.add(new Paragraph("Actividad del periodo", SECCION));
        documento.add(Chunk.NEWLINE);

        PdfPTable tabla = nuevaTabla();
        fila(tabla, "Cantidad de ingresos de madera", String.valueOf(r.getCantidadIngresos()));
        fila(tabla, "Cantidad de facturas de proveedor", String.valueOf(r.getCantidadFacturasProveedor()));
        fila(tabla, "Cantidad de facturas de cliente", String.valueOf(r.getCantidadFacturasCliente()));
        documento.add(tabla);
        documento.add(Chunk.NEWLINE);
    }

    private void agregarPie(Document documento) throws DocumentException {
        documento.add(Chunk.NEWLINE);
        LineSeparator linea = new LineSeparator();
        linea.setLineColor(Color.LIGHT_GRAY);
        documento.add(new Chunk(linea));
        Paragraph pie = new Paragraph(
                "Generado automaticamente por el sistema de gestion de ECOMADERAS ROSALES. "
                        + "Las facturas anuladas no se incluyen en este reporte.", PIE);
        pie.setSpacingBefore(6);
        documento.add(pie);
    }

    private PdfPTable nuevaTabla() throws DocumentException {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.4f, 1f});
        return tabla;
    }

    private void fila(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, ETIQUETA));
        c1.setBorderColor(new Color(228, 221, 208));
        c1.setPadding(6);
        c1.setBackgroundColor(new Color(243, 231, 211));

        PdfPCell c2 = new PdfPCell(new Phrase(valor, VALOR));
        c2.setBorderColor(new Color(228, 221, 208));
        c2.setPadding(6);

        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    private String formatear(LocalDate fecha) {
        return fecha != null ? fecha.format(FECHA_FMT) : "-";
    }

    private String diaSemana(LocalDate fecha) {
        if (fecha == null) return "-";
        String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return dia.substring(0, 1).toUpperCase() + dia.substring(1);
    }

    private String num(Double v) {
        double valor = v != null ? v : 0.0;
        return String.format(Locale.forLanguageTag("es-CR"), "%,.2f", valor);
    }

    private String moneda(Double v) {
        return "₡" + num(v);
    }
}
