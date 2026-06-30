package com.aserradero.controller;

import com.aserradero.dto.UsuarioRequest;
import com.aserradero.model.Usuario;
import com.aserradero.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestion de usuarios. Solo accesible para el rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioRequest request) {
        return new ResponseEntity<>(usuarioService.crear(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public Usuario desactivar(@PathVariable Long id) {
        return usuarioService.desactivar(id);
    }
}
