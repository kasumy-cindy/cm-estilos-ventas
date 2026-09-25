package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MetodoPagoRequest {
    @NotBlank
    @Size(max = 40)
    private String nombre;
    private Boolean activo = true;

    public MetodoPagoRequest() {
    }

    public MetodoPagoRequest(String nombre, Boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
