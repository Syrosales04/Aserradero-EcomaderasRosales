package com.aserradero.service;

import com.aserradero.dto.ProcesamientoMaderaRequest;
import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.model.IngresoMadera;
import com.aserradero.model.ProcesamientoMadera;
import com.aserradero.repository.IngresoMaderaRepository;
import com.aserradero.repository.ProcesamientoMaderaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProcesamientoMaderaService {

    private final ProcesamientoMaderaRepository procesamientoRepository;
    private final IngresoMaderaRepository ingresoRepository;
    private final ProductoMaderaService productoService;

    public ProcesamientoMaderaService(ProcesamientoMaderaRepository procesamientoRepository,
                                      IngresoMaderaRepository ingresoRepository,
                                      ProductoMaderaService productoService) {
        this.procesamientoRepository = procesamientoRepository;
        this.ingresoRepository = ingresoRepository;
        this.productoService = productoService;
    }

    public List<ProcesamientoMadera> listar() {
        return procesamientoRepository.findAll();
    }

    public ProcesamientoMadera buscarPorId(Long id) {
        return procesamientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Procesamiento no encontrado con id " + id));
    }

    @Transactional
    public ProcesamientoMadera registrar(ProcesamientoMaderaRequest req) {
        IngresoMadera ingreso = ingresoRepository.findById(req.getIdIngreso())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ingreso no encontrado con id " + req.getIdIngreso()));

        ProcesamientoMadera p = new ProcesamientoMadera();

        p.setIngreso(ingreso);
        p.setFecha(req.getFecha() != null ? req.getFecha() : LocalDate.now());
        p.setTipoMadera(ingreso.getTipoMadera());
        p.setProductoObtenido(req.getProductoObtenido());
        p.setPulgadasProcesadas(req.getPulgadasProcesadas());
        p.setObservacion(req.getObservacion());
        p.setEstado("ACTIVO");

        ProcesamientoMadera guardado = procesamientoRepository.save(p);

        productoService.ingresarAprovechable(
                guardado.getTipoMadera(),
                "pulgadas",
                guardado.getPulgadasProcesadas(),
                ingreso.getPrecioCompra(),
                ingreso.getPrecioVenta()
        );

        return guardado;
    }

    @Transactional
    public ProcesamientoMadera anular(Long id) {
    ProcesamientoMadera p = buscarPorId(id);

    if ("ANULADO".equalsIgnoreCase(p.getEstado())) {
        return p;
    }

    productoService.revertirProcesamiento(
            p.getTipoMadera(),
            p.getPulgadasProcesadas()
    );

    p.setEstado("ANULADO");
    return procesamientoRepository.save(p);
}

}