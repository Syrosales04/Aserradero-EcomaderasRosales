package com.aserradero.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturaProveedorRequest {

   @NotNull(message = "El proveedor es obligatorio")
private Long idProveedor;

@NotNull(message = "El ingreso de madera es obligatorio")
private Long idIngreso;
private LocalDate fechaFactura;

@NotBlank(message = "El número de factura es obligatorio")
private String numeroFactura;

private List<DetalleProveedorRequest> detalles = new ArrayList<>();

    public Long getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Long idProveedor) { this.idProveedor = idProveedor; }
    public Long getIdIngreso() { return idIngreso; }
    public void setIdIngreso(Long idIngreso) { this.idIngreso = idIngreso; }
    public LocalDate getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public List<DetalleProveedorRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleProveedorRequest> detalles) { this.detalles = detalles; }
}
