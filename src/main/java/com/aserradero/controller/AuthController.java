package com.aserradero.controller;

import com.aserradero.model.Usuario;
import com.aserradero.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Permite al frontend validar las credenciales y saber quien esta conectado.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Devuelve los datos del usuario autenticado.
     * El frontend llama a este endpoint con las credenciales para validar el login.
     */
    @GetMapping("/me")
    public Map<String, Object> usuarioActual(Principal principal) {
        Usuario usuario = usuarioService.buscarPorUsername(principal.getName());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("username", usuario.getUsername());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("rol", usuario.getRol().getNombre());
        return respuesta;
    }
}
