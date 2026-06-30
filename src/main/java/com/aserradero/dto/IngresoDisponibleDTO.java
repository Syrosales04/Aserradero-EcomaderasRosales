package com.aserradero.dto;

public class IngresoDisponibleDTO {

    private Long id;
    private String tipoMadera;
    private Double volumenTotal;
    private Double procesado;
    private Double disponible;

    public IngresoDisponibleDTO(Long id, String tipoMadera, Double volumenTotal, Double procesado, Double disponible) {
        this.id = id;
        this.tipoMadera = tipoMadera;
        this.volumenTotal = volumenTotal;
        this.procesado = procesado;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public String getTipoMadera() { return tipoMadera; }
    public Double getVolumenTotal() { return volumenTotal; }
    public Double getProcesado() { return procesado; }
    public Double getDisponible() { return disponible; }
}