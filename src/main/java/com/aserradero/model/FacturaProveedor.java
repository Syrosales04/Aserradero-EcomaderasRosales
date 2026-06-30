package com.aserradero.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Factura de proveedor: representa el costo de compra de la madera ingresada.
 */
@Entity
@Table(name = "facturas_proveedor")
public class FacturaProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ingreso")
    private IngresoMadera ingreso;

    @Column(name = "fecha_factura", nullable = false)
    private LocalDate fechaFactura;

    @Column(name = "numero_factura", length = 40)
    private String numeroFactura;

    private Double subtotal = 0.0;

    private Double total = 0.0;

    /** ACTIVA, ANULADA o PAGADA */
    @Column(nullable = false, length = 20)
    private String estado = "ACTIVA";

    @OneToMany(mappedBy = "facturaProveedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleFacturaProveedor> detalles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public IngresoMadera getIngreso() {
        return ingreso;
    }

    public void setIngreso(IngresoMadera ingreso) {
        this.ingreso = ingreso;
    }

    public LocalDate getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(LocalDate fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetalleFacturaProveedor> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaProveedor> detalles) {
        this.detalles = detalles;
    }
}
