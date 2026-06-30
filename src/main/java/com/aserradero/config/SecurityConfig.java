package com.aserradero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Configuracion de seguridad del sistema.
 * - Contrasenas encriptadas con BCrypt.
 * - Autenticacion HTTP Basic (el frontend la envia en cada peticion).
 * - Control de acceso por roles (ADMINISTRADOR, ENCARGADO).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // API REST: deshabilitamos CSRF (las credenciales viajan en cada peticion)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Recursos publicos: pantallas estaticas y login
                .requestMatchers("/", "/index.html", "/login.html",
                                 "/css/**", "/js/**", "/favicon.ico").permitAll()
                // Solo el ADMINISTRADOR gestiona usuarios y roles
                .requestMatchers("/api/usuarios/**", "/api/roles/**").hasRole("ADMINISTRADOR")
                // El resto de la API requiere estar autenticado
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            // Devuelve 401 en lugar de redirigir a una pagina de login HTML
            .exceptionHandling(e -> e.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .httpBasic(basic -> {});

        return http.build();
    }
}
