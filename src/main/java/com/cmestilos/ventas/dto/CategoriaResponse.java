package com.cmestilos.ventas.dto;

public class CategoriaResponse {
    private Integer idCategoria;
    private String nombre;

    public CategoriaResponse() {
    }

    public CategoriaResponse(Integer idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
