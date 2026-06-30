package com.aserradero.controller;

import com.aserradero.dto.FacturaProveedorRequest;
import com.aserradero.model.FacturaProveedor;
import com.aserradero.service.FacturaProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas-proveedor")
public class FacturaProveedorController {

    private final FacturaProveedorService facturaService;

    public FacturaProveedorController(FacturaProveedorService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public List<FacturaProveedor> listar() {
        return facturaService.listar();
    }

    @GetMapping("/{id}")
    public FacturaProveedor buscar(@PathVariable Long id) {
        return facturaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<FacturaProveedor> crear(@Valid @RequestBody FacturaProveedorRequest request) {
        return new ResponseEntity<>(facturaService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/anular")
    public FacturaProveedor anular(@PathVariable Long id) {
        return facturaService.anular(id);
    }
}
