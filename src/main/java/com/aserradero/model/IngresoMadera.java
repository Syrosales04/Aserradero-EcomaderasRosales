package com.aserradero.model;


import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Ingreso de madera: cada camion que llega al aserradero.
 * Guarda las medidas de la carga y el volumen calculado por cubicacion.
 *
 * Nota de diseno: ademas de los campos de la propuesta base, se agregan
 * tipoMadera, precioCompra y precioVenta para poder alimentar el inventario
 * con la madera aprovechable (Regla 9 / RF09).
 */
@Entity
@Table(name = "ingresos_madera")
public class IngresoMadera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "placa_camion", length = 20)
    private String placaCamion;

    // Medidas usadas para la cubicacion (en metros)
    @Column(name = "largo_camion", nullable = false)
    private Double largoCamion;

    @Column(name = "ancho_camion", nullable = false)
    private Double anchoCamion;

    @Column(name = "alto_carga", nullable = false)
    private Double altoCarga;

    /** Resultado de la cubicacion: largo x ancho x alto. */
    @Column(name = "volumen_total")
    private Double volumenTotal;

    // Datos para el inventario de la madera aprovechable
    @Column(name = "tipo_madera", length = 60)
    private String tipoMadera;

    @Column(name = "unidad_medida", length = 30)
    private String unidadMedida = "m3";

    @Column(name = "precio_compra")
    private Double precioCompra;

    @Column(name = "precio_venta")
    private Double precioVenta;

    /** REGISTRADO, PROCESADO o ANULADO */
    @Column(nullable = false, length = 20)
    private String estado = "REGISTRADO";

    @OneToOne(mappedBy = "ingreso", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Rendimiento rendimiento;

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

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getPlacaCamion() {
        return placaCamion;
    }

    public void setPlacaCamion(String placaCamion) {
        this.placaCamion = placaCamion;
    }

    public Double getLargoCamion() {
        return largoCamion;
    }

    public void setLargoCamion(Double largoCamion) {
        this.largoCamion = largoCamion;
    }

    public Double getAnchoCamion() {
        return anchoCamion;
    }

    public void setAnchoCamion(Double anchoCamion) {
        this.anchoCamion = anchoCamion;
    }

    public Double getAltoCarga() {
        return altoCarga;
    }

    public void setAltoCarga(Double altoCarga) {
        this.altoCarga = altoCarga;
    }

    public Double getVolumenTotal() {
        return volumenTotal;
    }

    public void setVolumenTotal(Double volumenTotal) {
        this.volumenTotal = volumenTotal;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Rendimiento getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(Rendimiento rendimiento) {
        this.rendimiento = rendimiento;
    }
}
