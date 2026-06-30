package com.aserradero.service;

import com.aserradero.dto.UsuarioRequest;
import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.exception.ReglaNegocioException;
import com.aserradero.model.Rol;
import com.aserradero.model.Usuario;
import com.aserradero.repository.RolRepository;
import com.aserradero.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Gestiona los usuarios y provee la autenticacion a Spring Security.
 */
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Spring Security usa este metodo para validar el login. */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (Boolean.FALSE.equals(usuario.getEstado())) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        String rol = "ROLE_" + usuario.getRol().getNombre();
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(rol)));
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));
    }

    public Usuario crear(UsuarioRequest req) {
        if (usuarioRepository.existsByUsername(req.getUsername())) {
            throw new ReglaNegocioException("Ya existe un usuario con ese nombre de usuario");
        }
        Rol rol = rolRepository.findById(req.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id " + req.getIdRol()));

        Usuario usuario = new Usuario();
        usuario.setUsername(req.getUsername());
        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        usuario.setNombre(req.getNombre());
        usuario.setRol(rol);
        usuario.setEstado(req.getEstado() != null ? req.getEstado() : true);
        return usuarioRepository.save(usuario);
    }

    public Usuario desactivar(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
        u.setEstado(false);
        return usuarioRepository.save(u);
    }
}
