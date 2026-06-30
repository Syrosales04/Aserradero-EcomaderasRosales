package com.aserradero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class ProcesamientoMaderaRequest {

    @NotNull(message = "El ingreso de madera es obligatorio")
    private Long idIngreso;

    private LocalDate fecha;

    private String tipoMadera;

    @NotBlank(message = "El producto obtenido es obligatorio")
    private String productoObtenido;

    @NotNull(message = "Las pulgadas procesadas son obligatorias")
    @Positive(message = "Las pulgadas procesadas deben ser mayores a cero")
    private Double pulgadasProcesadas;

    private String observacion;

    public Long getIdIngreso() { return idIngreso; }
    public void setIdIngreso(Long idIngreso) { this.idIngreso = idIngreso; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTipoMadera() { return tipoMadera; }
    public void setTipoMadera(String tipoMadera) { this.tipoMadera = tipoMadera; }

    public String getProductoObtenido() { return productoObtenido; }
    public void setProductoObtenido(String productoObtenido) { this.productoObtenido = productoObtenido; }

    public Double getPulgadasProcesadas() { return pulgadasProcesadas; }
    public void setPulgadasProcesadas(Double pulgadasProcesadas) { this.pulgadasProcesadas = pulgadasProcesadas; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}