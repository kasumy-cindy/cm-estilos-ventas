package com.cmestilos.ventas.dto;

public class UsuarioResponse {
    private Integer idUsuario;
    private String username;
    private String rol;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Integer idUsuario, String username, String rol) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.rol = rol;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
