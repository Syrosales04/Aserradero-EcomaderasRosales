package com.aserradero.service;

import com.aserradero.dto.IngresoDisponibleDTO;
import com.aserradero.dto.IngresoMaderaRequest;
import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.model.IngresoMadera;
import com.aserradero.model.ProcesamientoMadera;
import com.aserradero.model.Proveedor;
import com.aserradero.model.Rendimiento;
import com.aserradero.repository.IngresoMaderaRepository;
import com.aserradero.repository.ProcesamientoMaderaRepository;
import com.aserradero.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Registra cada camion de madera. Por cada ingreso:
 *  1. Calcula las pulgadas de madera en tuca.
 *  2. Calcula un rendimiento estimado 60/40.
 *
 * Nota:
 * El ingreso de madera ya NO aumenta inventario automáticamente.
 * El inventario se aumenta cuando se registra el procesamiento diario.
 */
@Service
public class IngresoMaderaService {

    private final IngresoMaderaRepository ingresoRepository;
    private final ProveedorRepository proveedorRepository;
    private final RendimientoService rendimientoService;
    private final ProcesamientoMaderaRepository procesamientoRepository;

    public IngresoMaderaService(IngresoMaderaRepository ingresoRepository,
                                ProveedorRepository proveedorRepository,
                                RendimientoService rendimientoService,
                                ProcesamientoMaderaRepository procesamientoRepository) {
        this.ingresoRepository = ingresoRepository;
        this.proveedorRepository = proveedorRepository;
        this.rendimientoService = rendimientoService;
        this.procesamientoRepository = procesamientoRepository;
    }

    public List<IngresoMadera> listar() {
        return ingresoRepository.findAll();
    }

    public List<IngresoDisponibleDTO> listarDisponiblesProcesamiento() {
        List<IngresoMadera> ingresos = ingresoRepository.findAll();
        List<ProcesamientoMadera> procesamientos = procesamientoRepository.findAll();

        List<IngresoDisponibleDTO> resultado = new ArrayList<>();

        for (IngresoMadera ingreso : ingresos) {
            if ("ANULADO".equalsIgnoreCase(ingreso.getEstado())) {
                continue;
            }

            double total = valor(ingreso.getVolumenTotal());

            double procesado = procesamientos.stream()
                    .filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado()))
                    .filter(p -> p.getIngreso() != null)
                    .filter(p -> p.getIngreso().getId().equals(ingreso.getId()))
                    .mapToDouble(p -> valor(p.getPulgadasProcesadas()))
                    .sum();

            double disponible = Math.max(total - procesado, 0);

            resultado.add(new IngresoDisponibleDTO(
                    ingreso.getId(),
                    ingreso.getTipoMadera(),
                    redondear(total),
                    redondear(procesado),
                    redondear(disponible)
            ));
        }

        return resultado;
    }

    public IngresoMadera buscarPorId(Long id) {
        return ingresoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ingreso no encontrado con id " + id));
    }

    @Transactional
    public IngresoMadera registrar(IngresoMaderaRequest req) {
        Proveedor proveedor = proveedorRepository.findById(req.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Proveedor no encontrado con id " + req.getIdProveedor()));

        IngresoMadera ingreso = new IngresoMadera();
        ingreso.setProveedor(proveedor);
        ingreso.setFechaIngreso(req.getFechaIngreso() != null ? req.getFechaIngreso() : LocalDate.now());
        ingreso.setPlacaCamion(req.getPlacaCamion());
        ingreso.setLargoCamion(req.getLargoCamion());
        ingreso.setAnchoCamion(req.getAnchoCamion());
        ingreso.setAltoCarga(req.getAltoCarga());
        ingreso.setTipoMadera(req.getTipoMadera());
        ingreso.setPrecioCompra(req.getPrecioCompra());
        ingreso.setPrecioVenta(req.getPrecioVenta());

        // 1. Calcular pulgadas de madera en tuca
        double pulgadas = rendimientoService.calcularPulgadas(
                req.getAltoCarga(),
                req.getLargoCamion(),
                req.getAnchoCamion()
        );

        pulgadas = redondear(pulgadas);

        ingreso.setVolumenTotal(pulgadas);
        ingreso.setUnidadMedida("pulgadas");

        // 2. Rendimiento estimado 60 / 40
        Rendimiento rendimiento = rendimientoService.calcular(ingreso);
        ingreso.setRendimiento(rendimiento);

        ingreso.setEstado("PROCESADO");

        // 3. Guardar ingreso.
        // No se aumenta inventario aquí.
        // El inventario se aumenta al registrar procesamiento diario.
        return ingresoRepository.save(ingreso);
    }

    public Rendimiento obtenerRendimiento(Long idIngreso) {
        IngresoMadera ingreso = buscarPorId(idIngreso);
        return ingreso.getRendimiento();
    }

    private double valor(Double d) {
        return d != null ? d : 0.0;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}