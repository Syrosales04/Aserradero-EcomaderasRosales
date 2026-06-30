package com.aserradero.service;

import com.aserradero.dto.DetalleClienteRequest;
import com.aserradero.dto.FacturaClienteRequest;
import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.model.*;
import com.aserradero.repository.ClienteRepository;
import com.aserradero.repository.FacturaClienteRepository;
import com.aserradero.repository.ProductoMaderaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Factura de cliente: registra la venta de madera.
 * Valida el inventario y lo descuenta. Si algo falla, la transaccion
 * completa se revierte (no se vende ni se descuenta nada).
 */
@Service
public class FacturaClienteService {

    private final FacturaClienteRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoMaderaRepository productoRepository;
    private final ProductoMaderaService productoService;

    public FacturaClienteService(FacturaClienteRepository facturaRepository,
                                 ClienteRepository clienteRepository,
                                 ProductoMaderaRepository productoRepository,
                                 ProductoMaderaService productoService) {
        this.facturaRepository = facturaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    public List<FacturaCliente> listar() {
        return facturaRepository.findAll();
    }

    public FacturaCliente buscarPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura de cliente no encontrada con id " + id));
    }

    @Transactional
    public FacturaCliente crear(FacturaClienteRequest req) {
        Cliente cliente = clienteRepository.findById(req.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con id " + req.getIdCliente()));

        FacturaCliente factura = new FacturaCliente();
        factura.setCliente(cliente);
        factura.setFechaFactura(req.getFechaFactura() != null ? req.getFechaFactura() : LocalDate.now());
        factura.setNumeroFactura(req.getNumeroFactura());
        factura.setEstado("ACTIVA");

        double subtotal = 0.0;
        for (DetalleClienteRequest d : req.getDetalles()) {
            ProductoMadera producto = productoRepository.findById(d.getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con id " + d.getIdProducto()));

            // Valida y descuenta inventario (Regla 1, 10 / RF11)
            productoService.descontar(producto, d.getCantidad());

            double precio = d.getPrecioUnitario() != null
                    ? d.getPrecioUnitario()
                    : (producto.getPrecioVenta() != null ? producto.getPrecioVenta() : 0.0);

            DetalleFacturaCliente detalle = new DetalleFacturaCliente();
            detalle.setFacturaCliente(factura);
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(precio);
            double sub = d.getCantidad() * precio;
            detalle.setSubtotal(sub);
            factura.getDetalles().add(detalle);
            subtotal += sub;
        }

        factura.setSubtotal(subtotal);
        factura.setTotal(subtotal); // sin impuestos en la version base
        return facturaRepository.save(factura);
    }

    @Transactional
public FacturaCliente anular(Long id) {

    FacturaCliente factura = buscarPorId(id);

    if ("ANULADA".equals(factura.getEstado())) {
        return factura;
    }

    for (DetalleFacturaCliente detalle : factura.getDetalles()) {

        ProductoMadera producto = detalle.getProducto();

        productoService.devolverInventario(
                producto,
                detalle.getCantidad()
        );
    }

    factura.setEstado("ANULADA");

    return facturaRepository.save(factura);
}
}
