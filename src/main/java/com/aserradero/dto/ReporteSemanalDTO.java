package com.aserradero.dto;

import java.time.LocalDate;

/**
 * Resumen de la actividad del aserradero en un rango de fechas.
 * Se usa para el dashboard y el reporte semanal.
 */
public class ReporteSemanalDTO {

    private LocalDate desde;
    private LocalDate hasta;

    private Double maderaIngresada = 0.0;
    private Double maderaAprovechable = 0.0;
    private Double desperdicio = 0.0;

    // Rendimiento REAL (según pulgadas realmente procesadas)
    private Double maderaProcesada = 0.0;
    private Double aprovechamientoReal = 0.0;        // %
    private Double desperdicioReal = 0.0;            // pulgadas
    private Double desperdicioRealPorcentaje = 0.0;  // %

    // Estándar de comparación (referencia, ya no obligatorio)
    private Double estandarEsperado = 60.0;          // %
    private Double maderaAprovechableEstandar = 0.0; // ingresada * 0.60

    private Double totalVentas = 0.0;
    private Double totalCompras = 0.0;

    // Ganancia actual: ventas realizadas - compras realizadas
    private Double gananciaSemanal = 0.0;

    // Nuevos indicadores de lógica del negocio
    private Double valorInventarioRestante = 0.0;
    private Double gananciaActual = 0.0;
    private Double gananciaEstimada = 0.0;
    private Double pendienteRecuperar = 0.0;

    private long cantidadFacturasCliente = 0;
    private long cantidadFacturasProveedor = 0;
    private long cantidadIngresos = 0;

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(LocalDate hasta) {
        this.hasta = hasta;
    }

    public Double getMaderaIngresada() {
        return maderaIngresada;
    }

    public void setMaderaIngresada(Double maderaIngresada) {
        this.maderaIngresada = maderaIngresada;
    }

    public Double getMaderaAprovechable() {
        return maderaAprovechable;
    }

    public void setMaderaAprovechable(Double maderaAprovechable) {
        this.maderaAprovechable = maderaAprovechable;
    }

    public Double getDesperdicio() {
        return desperdicio;
    }

    public void setDesperdicio(Double desperdicio) {
        this.desperdicio = desperdicio;
    }

    public Double getMaderaProcesada() {
        return maderaProcesada;
    }

    public void setMaderaProcesada(Double maderaProcesada) {
        this.maderaProcesada = maderaProcesada;
    }

    public Double getAprovechamientoReal() {
        return aprovechamientoReal;
    }

    public void setAprovechamientoReal(Double aprovechamientoReal) {
        this.aprovechamientoReal = aprovechamientoReal;
    }

    public Double getDesperdicioReal() {
        return desperdicioReal;
    }

    public void setDesperdicioReal(Double desperdicioReal) {
        this.desperdicioReal = desperdicioReal;
    }

    public Double getDesperdicioRealPorcentaje() {
        return desperdicioRealPorcentaje;
    }

    public void setDesperdicioRealPorcentaje(Double desperdicioRealPorcentaje) {
        this.desperdicioRealPorcentaje = desperdicioRealPorcentaje;
    }

    public Double getEstandarEsperado() {
        return estandarEsperado;
    }

    public void setEstandarEsperado(Double estandarEsperado) {
        this.estandarEsperado = estandarEsperado;
    }

    public Double getMaderaAprovechableEstandar() {
        return maderaAprovechableEstandar;
    }

    public void setMaderaAprovechableEstandar(Double maderaAprovechableEstandar) {
        this.maderaAprovechableEstandar = maderaAprovechableEstandar;
    }

    public Double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(Double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public Double getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(Double totalCompras) {
        this.totalCompras = totalCompras;
    }

    public Double getGananciaSemanal() {
        return gananciaSemanal;
    }

    public void setGananciaSemanal(Double gananciaSemanal) {
        this.gananciaSemanal = gananciaSemanal;
    }

    public Double getValorInventarioRestante() {
        return valorInventarioRestante;
    }

    public void setValorInventarioRestante(Double valorInventarioRestante) {
        this.valorInventarioRestante = valorInventarioRestante;
    }

    public Double getGananciaActual() {
        return gananciaActual;
    }

    public void setGananciaActual(Double gananciaActual) {
        this.gananciaActual = gananciaActual;
    }

    public Double getGananciaEstimada() {
        return gananciaEstimada;
    }

    public void setGananciaEstimada(Double gananciaEstimada) {
        this.gananciaEstimada = gananciaEstimada;
    }

    public Double getPendienteRecuperar() {
        return pendienteRecuperar;
    }

    public void setPendienteRecuperar(Double pendienteRecuperar) {
        this.pendienteRecuperar = pendienteRecuperar;
    }

    public long getCantidadFacturasCliente() {
        return cantidadFacturasCliente;
    }

    public void setCantidadFacturasCliente(long cantidadFacturasCliente) {
        this.cantidadFacturasCliente = cantidadFacturasCliente;
    }

    public long getCantidadFacturasProveedor() {
        return cantidadFacturasProveedor;
    }

    public void setCantidadFacturasProveedor(long cantidadFacturasProveedor) {
        this.cantidadFacturasProveedor = cantidadFacturasProveedor;
    }

    public long getCantidadIngresos() {
        return cantidadIngresos;
    }

    public void setCantidadIngresos(long cantidadIngresos) {
        this.cantidadIngresos = cantidadIngresos;
    }
}