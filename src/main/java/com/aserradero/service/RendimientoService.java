package com.aserradero.service;

import com.aserradero.model.IngresoMadera;
import com.aserradero.model.Rendimiento;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Calcula el rendimiento de la madera de un ingreso.
 *
 * Regla base del proyecto:
 *   madera_aprovechable = volumen_total * 0.60
 *   desperdicio         = volumen_total * 0.40
 */
@Service
public class RendimientoService {

    public static final double PORCENTAJE_APROVECHABLE = 0.60;
    public static final double PORCENTAJE_DESPERDICIO = 0.40;

    /**
     * Cubicacion simple: volumen = largo x ancho x alto.
     */
    public double calcularPulgadas(double altoCamion, double largoCamion, double largoTuca) {
    return altoCamion * largoCamion * largoTuca * 0.56 * 362;
}

    /**
     * Crea el objeto Rendimiento a partir de un ingreso ya cubicado.
     */
    public Rendimiento calcular(IngresoMadera ingreso) {
        double volumen = ingreso.getVolumenTotal() != null ? ingreso.getVolumenTotal() : 0.0;

        Rendimiento r = new Rendimiento();
        r.setIngreso(ingreso);
        r.setVolumenTotal(volumen);
        r.setPorcentajeAprovechable(PORCENTAJE_APROVECHABLE * 100);   // 60
        r.setPorcentajeDesperdicio(PORCENTAJE_DESPERDICIO * 100);     // 40
        r.setVolumenAprovechable(redondear(volumen * PORCENTAJE_APROVECHABLE));
        r.setVolumenDesperdicio(redondear(volumen * PORCENTAJE_DESPERDICIO));
        r.setFechaCalculo(LocalDate.now());
        return r;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
