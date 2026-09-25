package com.cmestilos.ventas.dto;

import java.time.LocalDateTime;

public class ResenaResponse {
    private Integer idResena;
    private String nombreCliente;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaHora;

    public ResenaResponse() {
    }

    public ResenaResponse(Integer idResena, String nombreCliente, Integer puntuacion,
                          String comentario, LocalDateTime fechaHora) {
        this.idResena = idResena;
        this.nombreCliente = nombreCliente;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fechaHora = fechaHora;
    }

    public Integer getIdResena() { return idResena; }
    public void setIdResena(Integer idResena) { this.idResena = idResena; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
