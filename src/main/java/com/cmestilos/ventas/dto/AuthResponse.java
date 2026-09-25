package com.cmestilos.ventas.dto;

public class AuthResponse {
    private String token;
    private String tipo;
    private Integer usuarioId;
    private String username;
    private String rol;

    public AuthResponse() {
    }

    public AuthResponse(String token, String tipo, Integer usuarioId, String username, String rol) {
        this.token = token;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.username = username;
        this.rol = rol;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
