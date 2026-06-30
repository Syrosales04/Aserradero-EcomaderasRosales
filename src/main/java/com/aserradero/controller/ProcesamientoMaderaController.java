package com.aserradero.controller;

import com.aserradero.dto.ProcesamientoMaderaRequest;
import com.aserradero.model.ProcesamientoMadera;
import com.aserradero.service.ProcesamientoMaderaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procesamientos")
public class ProcesamientoMaderaController {

    private final ProcesamientoMaderaService procesamientoService;

    public ProcesamientoMaderaController(ProcesamientoMaderaService procesamientoService) {
        this.procesamientoService = procesamientoService;
    }

    @GetMapping
    public List<ProcesamientoMadera> listar() {
        return procesamientoService.listar();
    }

    @PostMapping
    public ProcesamientoMadera registrar(@Valid @RequestBody ProcesamientoMaderaRequest request) {
        return procesamientoService.registrar(request);
    }

    @PutMapping("/{id}/anular")
    public ProcesamientoMadera anular(@PathVariable Long id) {
        return procesamientoService.anular(id);
    }
}