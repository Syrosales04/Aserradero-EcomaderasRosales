package com.aserradero.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Rendimiento de la madera de un ingreso.
 * Regla base del proyecto: 60% aprovechable, 40% desperdicio.
 */
@Entity
@Table(name = "rendimientos")
public class Rendimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ingreso", nullable = false, unique = true)
    @JsonIgnore
    private IngresoMadera ingreso;

    @Column(name = "volumen_total")
    private Double volumenTotal;

    @Column(name = "porcentaje_aprovechable")
    private Double porcentajeAprovechable;

    @Column(name = "volumen_aprovechable")
    private Double volumenAprovechable;

    @Column(name = "porcentaje_desperdicio")
    private Double porcentajeDesperdicio;

    @Column(name = "volumen_desperdicio")
    private Double volumenDesperdicio;

    @Column(name = "fecha_calculo")
    private LocalDate fechaCalculo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IngresoMadera getIngreso() {
        return ingreso;
    }

    public void setIngreso(IngresoMadera ingreso) {
        this.ingreso = ingreso;
    }

    public Double getVolumenTotal() {
        return volumenTotal;
    }

    public void setVolumenTotal(Double volumenTotal) {
        this.volumenTotal = volumenTotal;
    }

    public Double getPorcentajeAprovechable() {
        return porcentajeAprovechable;
    }

    public void setPorcentajeAprovechable(Double porcentajeAprovechable) {
        this.porcentajeAprovechable = porcentajeAprovechable;
    }

    public Double getVolumenAprovechable() {
        return volumenAprovechable;
    }

    public void setVolumenAprovechable(Double volumenAprovechable) {
        this.volumenAprovechable = volumenAprovechable;
    }

    public Double getPorcentajeDesperdicio() {
        return porcentajeDesperdicio;
    }

    public void setPorcentajeDesperdicio(Double porcentajeDesperdicio) {
        this.porcentajeDesperdicio = porcentajeDesperdicio;
    }

    public Double getVolumenDesperdicio() {
        return volumenDesperdicio;
    }

    public void setVolumenDesperdicio(Double volumenDesperdicio) {
        this.volumenDesperdicio = volumenDesperdicio;
    }

    public LocalDate getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDate fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }
}
