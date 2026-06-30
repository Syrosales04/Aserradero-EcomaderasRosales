package com.aserradero.controller;

import com.aserradero.dto.ReporteSemanalDTO;
import com.aserradero.service.ReportePdfService;
import com.aserradero.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReportePdfService reportePdfService;

    public ReporteController(ReporteService reporteService, ReportePdfService reportePdfService) {
        this.reporteService = reporteService;
        this.reportePdfService = reportePdfService;
    }

    /** Reporte de la semana actual (lunes a domingo). Util para el dashboard. */
    @GetMapping("/semana-actual")
    public ReporteSemanalDTO semanaActual() {
        return reporteService.reporteSemanaActual();
    }

    /** Reporte para un rango de fechas: /api/reportes?desde=2025-01-01&hasta=2025-01-07 */
    @GetMapping
    public ReporteSemanalDTO porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.generar(desde, hasta);
    }

    /** Descarga el reporte semanal en PDF: /api/reportes/pdf?desde=2025-01-01&hasta=2025-01-07 */
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        ReporteSemanalDTO reporte = reporteService.generar(desde, hasta);
        byte[] pdf = reportePdfService.generarReporteSemanal(reporte);

        String nombreArchivo = "reporte-ecomaderas-rosales-" + desde + "_" + hasta + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(nombreArchivo).build());

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
