package com.aserradero.controller;

import com.aserradero.dto.IngresoDisponibleDTO;
import com.aserradero.dto.IngresoMaderaRequest;
import com.aserradero.model.IngresoMadera;
import com.aserradero.model.Rendimiento;
import com.aserradero.service.IngresoMaderaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoMaderaController {

    private final IngresoMaderaService ingresoService;

    public IngresoMaderaController(IngresoMaderaService ingresoService) {
        this.ingresoService = ingresoService;
    }

   @GetMapping
public List<IngresoMadera> listar() {
    return ingresoService.listar();
}

@GetMapping("/disponibles-procesamiento")
public List<IngresoDisponibleDTO> listarDisponiblesProcesamiento() {
    return ingresoService.listarDisponiblesProcesamiento();
}

@GetMapping("/{id}")
public IngresoMadera buscar(@PathVariable Long id) {
    return ingresoService.buscarPorId(id);
}

    /** Registra el camion. El sistema calcula cubicacion + rendimiento + inventario. */
    @PostMapping
    public ResponseEntity<IngresoMadera> registrar(@Valid @RequestBody IngresoMaderaRequest request) {
        return new ResponseEntity<>(ingresoService.registrar(request), HttpStatus.CREATED);
    }

    /** Devuelve el rendimiento (60/40) calculado para ese ingreso. */
    @GetMapping("/{id}/rendimiento")
    public Rendimiento rendimiento(@PathVariable Long id) {
        return ingresoService.obtenerRendimiento(id);
    }
}
