package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResenaRequest {
    @NotBlank
    @Size(max = 100)
    private String nombreCliente;

    @Email
    @Size(max = 100)
    private String correo;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer puntuacion;

    @NotBlank
    @Size(max = 500)
    private String comentario;

    public ResenaRequest() {
    }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
