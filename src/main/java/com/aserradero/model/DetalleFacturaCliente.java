package com.aserradero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Detalle de una factura de cliente: una linea de madera vendida.
 */
@Entity
@Table(name = "detalle_factura_cliente")
public class DetalleFacturaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_factura_cliente", nullable = false)
    @JsonIgnore
    private FacturaCliente facturaCliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private ProductoMadera producto;

    private Double cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    private Double subtotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FacturaCliente getFacturaCliente() {
        return facturaCliente;
    }

    public void setFacturaCliente(FacturaCliente facturaCliente) {
        this.facturaCliente = facturaCliente;
    }

    public ProductoMadera getProducto() {
        return producto;
    }

    public void setProducto(ProductoMadera producto) {
        this.producto = producto;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
