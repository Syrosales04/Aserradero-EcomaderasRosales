package com.aserradero.dto;

public class DetalleProveedorRequest {
    private String tipoMadera;
    private Double cantidad;
    private Double precioUnitario;

    public String getTipoMadera() { return tipoMadera; }
    public void setTipoMadera(String tipoMadera) { this.tipoMadera = tipoMadera; }
    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}
