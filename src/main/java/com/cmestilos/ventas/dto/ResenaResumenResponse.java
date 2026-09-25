package com.cmestilos.ventas.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResenaResumenResponse {
    private Integer productoId;
    private BigDecimal promedio;
    private Integer cantidad;
    private List<ResenaResponse> resenas = new ArrayList<>();

    public ResenaResumenResponse() {
    }

    public ResenaResumenResponse(Integer productoId, BigDecimal promedio, Integer cantidad,
                                 List<ResenaResponse> resenas) {
        this.productoId = productoId;
        this.promedio = promedio;
        this.cantidad = cantidad;
        this.resenas = resenas;
    }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public BigDecimal getPromedio() { return promedio; }
    public void setPromedio(BigDecimal promedio) { this.promedio = promedio; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public List<ResenaResponse> getResenas() { return resenas; }
    public void setResenas(List<ResenaResponse> resenas) { this.resenas = resenas; }
}
