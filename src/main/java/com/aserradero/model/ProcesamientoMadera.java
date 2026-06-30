package com.aserradero.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "procesamientos_madera")
public class ProcesamientoMadera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ingreso", nullable = false)
    private IngresoMadera ingreso;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "tipo_madera", nullable = false, length = 60)
    private String tipoMadera;

    @Column(name = "producto_obtenido", nullable = false, length = 80)
    private String productoObtenido;

    @Column(name = "pulgadas_procesadas", nullable = false)
    private Double pulgadasProcesadas;

    @Column(length = 255)
    private String observacion;

    @Column(nullable = false, length = 20)
    private String estado = "ACTIVO";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IngresoMadera getIngreso() { return ingreso; }
    public void setIngreso(IngresoMadera ingreso) { this.ingreso = ingreso; }

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

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}