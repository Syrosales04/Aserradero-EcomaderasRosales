package com.aserradero.controller;

import com.aserradero.model.ProductoMadera;
import com.aserradero.service.ProductoMaderaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoMaderaController {

    private final ProductoMaderaService productoService;

    public ProductoMaderaController(ProductoMaderaService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoMadera> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ProductoMadera buscar(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ProductoMadera> crear(@Valid @RequestBody ProductoMadera producto) {
        return new ResponseEntity<>(productoService.crear(producto), HttpStatus.CREATED);
    }
}
