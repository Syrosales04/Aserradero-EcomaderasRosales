package com.aserradero.service;

import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.exception.ReglaNegocioException;
import com.aserradero.model.ProductoMadera;
import com.aserradero.repository.ProductoMaderaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductoMaderaService {

    private final ProductoMaderaRepository productoRepository;

    public ProductoMaderaService(ProductoMaderaRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoMadera> listar() {
        return productoRepository.findAll();
    }

    public ProductoMadera buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
    }

    public ProductoMadera crear(ProductoMadera producto) {
        producto.setId(null);
        if (producto.getFechaRegistro() == null) {
            producto.setFechaRegistro(LocalDate.now());
        }
        if (producto.getCantidadDisponible() == null) {
            producto.setCantidadDisponible(producto.getCantidadTotal() != null ? producto.getCantidadTotal() : 0.0);
        }
        return productoRepository.save(producto);
    }

    /**
     * Suma madera aprovechable al inventario.
     * Si ya existe un producto de ese tipo de madera, le aumenta la cantidad;
     * si no existe, crea uno nuevo. (Regla 9 / RF09)
     */
    public ProductoMadera ingresarAprovechable(String tipoMadera, String unidadMedida, Double cantidad,
                                               Double precioCompra, Double precioVenta) {
        if (tipoMadera == null || tipoMadera.isBlank()) {
            tipoMadera = "Sin clasificar";
        }
        final String tipo = tipoMadera;
        ProductoMadera producto = productoRepository.findByTipoMaderaIgnoreCase(tipo)
                .orElseGet(() -> {
                    ProductoMadera nuevo = new ProductoMadera();
                    nuevo.setTipoMadera(tipo);
                    nuevo.setUnidadMedida(unidadMedida != null ? unidadMedida : "m3");
                    nuevo.setCantidadTotal(0.0);
                    nuevo.setCantidadDisponible(0.0);
                    nuevo.setFechaRegistro(LocalDate.now());
                    return nuevo;
                });

        producto.setCantidadTotal(producto.getCantidadTotal() + cantidad);
        producto.setCantidadDisponible(producto.getCantidadDisponible() + cantidad);
        if (precioCompra != null) producto.setPrecioCompra(precioCompra);
        if (precioVenta != null) producto.setPrecioVenta(precioVenta);
        producto.setEstado(producto.getCantidadDisponible() > 0 ? "DISPONIBLE" : "AGOTADO");
        return productoRepository.save(producto);
    }

    /** Descuenta del inventario lo vendido. Valida que haya suficiente (Regla 1 / RF11). */
    public void descontar(ProductoMadera producto, Double cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad a vender debe ser mayor a 0");
        }
        if (producto.getCantidadDisponible() < cantidad) {
            throw new ReglaNegocioException(
                    "No hay suficiente madera de " + producto.getTipoMadera() +
                    ". Disponible: " + producto.getCantidadDisponible() + ", solicitado: " + cantidad);
        }
        producto.setCantidadDisponible(producto.getCantidadDisponible() - cantidad);
        producto.setEstado(producto.getCantidadDisponible() > 0 ? "DISPONIBLE" : "AGOTADO");
        productoRepository.save(producto);
    }

    public void revertirProcesamiento(String tipoMadera, Double cantidad) {
    if (tipoMadera == null || tipoMadera.isBlank()) {
        throw new ReglaNegocioException("El tipo de madera es obligatorio");
    }

    if (cantidad == null || cantidad <= 0) {
        throw new ReglaNegocioException("La cantidad a revertir debe ser mayor a 0");
    }

    ProductoMadera producto = productoRepository.findByTipoMaderaIgnoreCase(tipoMadera)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe inventario para la madera " + tipoMadera));

    producto.setCantidadTotal(Math.max(producto.getCantidadTotal() - cantidad, 0));
    producto.setCantidadDisponible(Math.max(producto.getCantidadDisponible() - cantidad, 0));

    producto.setEstado(producto.getCantidadDisponible() > 0 ? "DISPONIBLE" : "AGOTADO");

    productoRepository.save(producto);
}

public void devolverInventario(ProductoMadera producto, Double cantidad) {

    if (cantidad == null || cantidad <= 0) {
        throw new ReglaNegocioException(
                "La cantidad a devolver debe ser mayor a 0");
    }

    producto.setCantidadDisponible(
            producto.getCantidadDisponible() + cantidad);

    producto.setEstado("DISPONIBLE");

    productoRepository.save(producto);
}


}
