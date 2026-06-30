package com.aserradero.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Producto de madera disponible en inventario.
 * Aumenta cuando entra madera aprovechable y disminuye cuando se vende.
 */
@Entity
@Table(name = "productos_madera")
public class ProductoMadera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_madera", nullable = false, length = 60)
    private String tipoMadera;

    @Column(name = "unidad_medida", length = 30)
    private String unidadMedida = "m3";

    @Column(name = "cantidad_total", nullable = false)
    private Double cantidadTotal = 0.0;

    @Column(name = "cantidad_disponible", nullable = false)
    private Double cantidadDisponible = 0.0;

    @Column(name = "precio_compra")
    private Double precioCompra;

    @Column(name = "precio_venta")
    private Double precioVenta;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    /** DISPONIBLE, AGOTADO o INACTIVO */
    @Column(nullable = false, length = 20)
    private String estado = "DISPONIBLE";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoMadera() {
        return tipoMadera;
    }

    public void setTipoMadera(String tipoMadera) {
        this.tipoMadera = tipoMadera;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Double getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Double cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public Double getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Double cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
