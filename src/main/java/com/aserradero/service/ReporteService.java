package com.aserradero.service;

import com.aserradero.dto.ReporteSemanalDTO;
import com.aserradero.model.FacturaCliente;
import com.aserradero.model.FacturaProveedor;
import com.aserradero.model.IngresoMadera;
import com.aserradero.model.ProcesamientoMadera;
import com.aserradero.model.ProductoMadera;
import com.aserradero.repository.FacturaClienteRepository;
import com.aserradero.repository.FacturaProveedorRepository;
import com.aserradero.repository.IngresoMaderaRepository;
import com.aserradero.repository.ProcesamientoMaderaRepository;
import com.aserradero.repository.ProductoMaderaRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class ReporteService {

    private final IngresoMaderaRepository ingresoRepository;
    private final FacturaClienteRepository facturaClienteRepository;
    private final FacturaProveedorRepository facturaProveedorRepository;
    private final ProductoMaderaRepository productoRepository;
    private final ProcesamientoMaderaRepository procesamientoRepository;

    public ReporteService(IngresoMaderaRepository ingresoRepository,
                          FacturaClienteRepository facturaClienteRepository,
                          FacturaProveedorRepository facturaProveedorRepository,
                          ProductoMaderaRepository productoRepository,
                          ProcesamientoMaderaRepository procesamientoRepository) {
        this.ingresoRepository = ingresoRepository;
        this.facturaClienteRepository = facturaClienteRepository;
        this.facturaProveedorRepository = facturaProveedorRepository;
        this.productoRepository = productoRepository;
        this.procesamientoRepository = procesamientoRepository;
    }

    public ReporteSemanalDTO reporteSemanaActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return generar(lunes, domingo);
    }

    public ReporteSemanalDTO generar(LocalDate desde, LocalDate hasta) {
        ReporteSemanalDTO dto = new ReporteSemanalDTO();
        dto.setDesde(desde);
        dto.setHasta(hasta);

        List<IngresoMadera> ingresos = ingresoRepository.findByFechaIngresoBetween(desde, hasta);

        double ingresada = 0;
        for (IngresoMadera i : ingresos) {
            if ("ANULADO".equalsIgnoreCase(i.getEstado())) {
                continue;
            }
            ingresada += valor(i.getVolumenTotal());
        }

        // Madera realmente procesada en el rango (solo procesamientos activos)
        List<ProcesamientoMadera> procesamientos =
                procesamientoRepository.findByEstadoAndFechaBetween("ACTIVO", desde, hasta);

        double procesada = procesamientos.stream()
                .mapToDouble(p -> valor(p.getPulgadasProcesadas()))
                .sum();

        // Rendimiento REAL = procesada / ingresada
        double aprovechamientoReal = ingresada > 0 ? (procesada / ingresada) * 100 : 0;
        double desperdicioReal = Math.max(ingresada - procesada, 0);
        double desperdicioRealPorcentaje = ingresada > 0 ? (desperdicioReal / ingresada) * 100 : 0;

        // Estándar de comparación (referencia 60%)
        double aprovechableEstandar = ingresada * 0.60;

        dto.setMaderaIngresada(redondear(ingresada));
        dto.setMaderaProcesada(redondear(procesada));
        dto.setAprovechamientoReal(redondear(aprovechamientoReal));
        dto.setDesperdicioReal(redondear(desperdicioReal));
        dto.setDesperdicioRealPorcentaje(redondear(desperdicioRealPorcentaje));
        dto.setEstandarEsperado(60.0);
        dto.setMaderaAprovechableEstandar(redondear(aprovechableEstandar));

        // Compatibilidad: los campos antiguos ahora reflejan el dato REAL
        dto.setMaderaAprovechable(redondear(procesada));
        dto.setDesperdicio(redondear(desperdicioReal));
        dto.setCantidadIngresos(ingresos.size());

        List<FacturaCliente> ventas = facturaClienteRepository
                .findByEstadoAndFechaFacturaBetween("ACTIVA", desde, hasta);

        double totalVentas = ventas.stream()
                .mapToDouble(f -> valor(f.getTotal()))
                .sum();

        dto.setTotalVentas(redondear(totalVentas));
        dto.setCantidadFacturasCliente(ventas.size());
        

        List<FacturaProveedor> compras = facturaProveedorRepository.findAll();

        double totalCompras = compras.stream()
        .filter(f -> "ACTIVA".equalsIgnoreCase(f.getEstado()))
        .filter(f -> f.getIngreso() != null)
        .filter(f -> {
            LocalDate fechaIngreso = f.getIngreso().getFechaIngreso();
            return fechaIngreso != null
                    && !fechaIngreso.isBefore(desde)
                    && !fechaIngreso.isAfter(hasta);
        })
        .mapToDouble(f -> valor(f.getTotal()))
        .sum();

        long cantidadCompras = compras.stream()
        .filter(f -> "ACTIVA".equalsIgnoreCase(f.getEstado()))
        .filter(f -> f.getIngreso() != null)
        .filter(f -> {
            LocalDate fechaIngreso = f.getIngreso().getFechaIngreso();
            return fechaIngreso != null
                    && !fechaIngreso.isBefore(desde)
                    && !fechaIngreso.isAfter(hasta);
        })
        .count();

        dto.setTotalCompras(redondear(totalCompras));
        dto.setCantidadFacturasProveedor(cantidadCompras);

        dto.setTotalCompras(redondear(totalCompras));
        dto.setCantidadFacturasProveedor(compras.size());

        double gananciaActual = totalVentas - totalCompras;

        double valorInventarioRestante = calcularValorInventarioRestante();

        double gananciaEstimada = totalVentas + valorInventarioRestante - totalCompras;

        double pendienteRecuperar = Math.max(totalCompras - totalVentas, 0);

        dto.setGananciaSemanal(redondear(gananciaActual));
        dto.setGananciaActual(redondear(gananciaActual));
        dto.setValorInventarioRestante(redondear(valorInventarioRestante));
        dto.setGananciaEstimada(redondear(gananciaEstimada));
        dto.setPendienteRecuperar(redondear(pendienteRecuperar));

        return dto;
    }

    private double calcularValorInventarioRestante() {
        List<ProductoMadera> productos = productoRepository.findAll();

        return productos.stream()
                .filter(p -> !"ANULADO".equalsIgnoreCase(p.getEstado()))
                .mapToDouble(p ->
                        valor(p.getCantidadDisponible()) * valor(p.getPrecioVenta())
                )
                .sum();
    }

    private double valor(Double d) {
        return d != null ? d : 0.0;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}