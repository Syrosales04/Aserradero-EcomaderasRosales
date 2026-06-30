package com.aserradero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturaClienteRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    private LocalDate fechaFactura;

    @NotBlank(message = "El número de factura es obligatorio")
    private String numeroFactura;

    @NotEmpty(message = "La factura debe tener al menos un producto")
    private List<DetalleClienteRequest> detalles = new ArrayList<>();

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public LocalDate getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public List<DetalleClienteRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleClienteRequest> detalles) { this.detalles = detalles; }
}
