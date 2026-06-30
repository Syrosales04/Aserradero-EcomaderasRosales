package com.aserradero.service;

import com.aserradero.dto.FacturaProveedorRequest;
import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.exception.ReglaNegocioException;
import com.aserradero.model.*;
import com.aserradero.repository.FacturaProveedorRepository;
import com.aserradero.repository.IngresoMaderaRepository;
import com.aserradero.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Factura de proveedor: registra el costo de compra de la madera.
 */
@Service
public class FacturaProveedorService {

    private final FacturaProveedorRepository facturaRepository;
    private final ProveedorRepository proveedorRepository;
    private final IngresoMaderaRepository ingresoRepository;

    public FacturaProveedorService(FacturaProveedorRepository facturaRepository,
                                   ProveedorRepository proveedorRepository,
                                   IngresoMaderaRepository ingresoRepository) {
        this.facturaRepository = facturaRepository;
        this.proveedorRepository = proveedorRepository;
        this.ingresoRepository = ingresoRepository;
    }

    public List<FacturaProveedor> listar() {
        return facturaRepository.findAll();
    }

    public FacturaProveedor buscarPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura de proveedor no encontrada con id " + id));
    }

   @Transactional
public FacturaProveedor crear(FacturaProveedorRequest req) {
    Proveedor proveedor = proveedorRepository.findById(req.getIdProveedor())
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Proveedor no encontrado con id " + req.getIdProveedor()));

    // El ingreso de madera es obligatorio: la factura toma de ahí sus datos.
    if (req.getIdIngreso() == null) {
        throw new ReglaNegocioException("Debe seleccionar un ingreso de madera");
    }

    IngresoMadera ingreso = ingresoRepository.findById(req.getIdIngreso())
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "Ingreso no encontrado con id " + req.getIdIngreso()));

    // Regla: un ingreso no puede tener dos facturas (salvo que la anterior esté anulada).
    if (facturaRepository.existsByIngresoIdAndEstadoNot(ingreso.getId(), "ANULADA")) {
        throw new ReglaNegocioException(
                "El ingreso #" + ingreso.getId() + " ya tiene una factura de proveedor asociada");
    }

    FacturaProveedor factura = new FacturaProveedor();
    factura.setProveedor(proveedor);
    factura.setFechaFactura(req.getFechaFactura() != null ? req.getFechaFactura() : LocalDate.now());
    factura.setNumeroFactura(req.getNumeroFactura());
    factura.setEstado("ACTIVA");
    factura.setIngreso(ingreso);

    // Total compra = pulgadas ingresadas × precio compra
    double cantidadPulgadas = ingreso.getVolumenTotal() != null ? ingreso.getVolumenTotal() : 0.0;
    double precioCompra = ingreso.getPrecioCompra() != null ? ingreso.getPrecioCompra() : 0.0;
    double subtotal = cantidadPulgadas * precioCompra;

    DetalleFacturaProveedor detalle = new DetalleFacturaProveedor();
    detalle.setFacturaProveedor(factura);
    detalle.setTipoMadera(ingreso.getTipoMadera());
    detalle.setCantidad(cantidadPulgadas);
    detalle.setPrecioUnitario(precioCompra);
    detalle.setSubtotal(subtotal);
    factura.getDetalles().add(detalle);

    factura.setSubtotal(subtotal);
    factura.setTotal(subtotal);

    return facturaRepository.save(factura);
}

    public FacturaProveedor anular(Long id) {
        FacturaProveedor factura = buscarPorId(id);
        factura.setEstado("ANULADA");
        return facturaRepository.save(factura);
    }
}
