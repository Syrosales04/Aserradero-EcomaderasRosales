package com.aserradero.controller;

import com.aserradero.dto.FacturaClienteRequest;
import com.aserradero.model.FacturaCliente;
import com.aserradero.service.FacturaClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas-cliente")
public class FacturaClienteController {

    private final FacturaClienteService facturaService;

    public FacturaClienteController(FacturaClienteService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public List<FacturaCliente> listar() {
        return facturaService.listar();
    }

    @GetMapping("/{id}")
    public FacturaCliente buscar(@PathVariable Long id) {
        return facturaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<FacturaCliente> crear(@Valid @RequestBody FacturaClienteRequest request) {
        return new ResponseEntity<>(facturaService.crear(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/anular")
    public FacturaCliente anular(@PathVariable Long id) {
        return facturaService.anular(id);
    }
}
