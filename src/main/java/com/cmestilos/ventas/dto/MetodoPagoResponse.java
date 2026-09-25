package com.cmestilos.ventas.dto;

public class MetodoPagoResponse {
    private Integer idMetodoPago;
    private String nombre;
    private Boolean activo;

    public MetodoPagoResponse() {
    }

    public MetodoPagoResponse(Integer idMetodoPago, String nombre, Boolean activo) {
        this.idMetodoPago = idMetodoPago;
        this.nombre = nombre;
        this.activo = activo;
    }

    public Integer getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(Integer idMetodoPago) { this.idMetodoPago = idMetodoPago; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
