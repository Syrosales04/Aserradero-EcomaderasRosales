package com.aserradero.config;

import com.aserradero.model.Rol;
import com.aserradero.model.Usuario;
import com.aserradero.repository.RolRepository;
import com.aserradero.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Al arrancar la aplicacion crea los roles y los usuarios por defecto
 * si todavia no existen. Asi puedes entrar al sistema desde el primer momento.
 *
 *   admin     / admin123      (ADMINISTRADOR)
 *   encargado / encargado123  (ENCARGADO)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol admin = rolRepository.findByNombre("ADMINISTRADOR")
                .orElseGet(() -> rolRepository.save(new Rol("ADMINISTRADOR")));
        Rol encargado = rolRepository.findByNombre("ENCARGADO")
                .orElseGet(() -> rolRepository.save(new Rol("ENCARGADO")));

        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario u = new Usuario();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setNombre("Administrador del sistema");
            u.setEstado(true);
            u.setRol(admin);
            usuarioRepository.save(u);
        }

        if (usuarioRepository.findByUsername("encargado").isEmpty()) {
            Usuario u = new Usuario();
            u.setUsername("encargado");
            u.setPassword(passwordEncoder.encode("encargado123"));
            u.setNombre("Encargado del aserradero");
            u.setEstado(true);
            u.setRol(encargado);
            usuarioRepository.save(u);
        }
    }
}
