package com.aserradero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioRequest {
    @NotBlank(message = "El usuario es obligatorio")
    private String username;
    @NotBlank(message = "La contrasena es obligatoria")
    private String password;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotNull(message = "El rol es obligatorio")
    private Long idRol;
    private Boolean estado = true;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
