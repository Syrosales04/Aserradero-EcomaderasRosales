package com.aserradero.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Datos que envia el frontend para registrar un ingreso de madera.
 * El sistema calcula automaticamente el volumen (cubicacion) y el rendimiento.
 */
public class IngresoMaderaRequest {

    @NotNull(message = "El proveedor es obligatorio")
    private Long idProveedor;

    private LocalDate fechaIngreso;

    private String placaCamion;

    @NotNull(message = "El largo es obligatorio")
    @Positive(message = "El largo debe ser mayor a 0")
    private Double largoCamion;

    @NotNull(message = "El ancho es obligatorio")
    @Positive(message = "El ancho debe ser mayor a 0")
    private Double anchoCamion;

    @NotNull(message = "El alto es obligatorio")
    @Positive(message = "El alto debe ser mayor a 0")
    private Double altoCarga;

    private String tipoMadera;
    private String unidadMedida;
    private Double precioCompra;
    private Double precioVenta;

    public Long getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public String getPlacaCamion() { return placaCamion; }
    public void setPlacaCamion(String placaCamion) { this.placaCamion = placaCamion; }
    public Double getLargoCamion() { return largoCamion; }
    public void setLargoCamion(Double largoCamion) { this.largoCamion = largoCamion; }
    public Double getAnchoCamion() { return anchoCamion; }
    public void setAnchoCamion(Double anchoCamion) { this.anchoCamion = anchoCamion; }
    public Double getAltoCarga() { return altoCarga; }
    public void setAltoCarga(Double altoCarga) { this.altoCarga = altoCarga; }
    public String getTipoMadera() { return tipoMadera; }
    public void setTipoMadera(String tipoMadera) { this.tipoMadera = tipoMadera; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
}
