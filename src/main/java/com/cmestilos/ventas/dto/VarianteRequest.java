package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VarianteRequest {
    @NotNull
    private Integer productoId;

    @NotBlank
    @Size(max = 10)
    private String talla;

    @NotBlank
    @Size(max = 30)
    private String color;

    @NotNull
    @Min(0)
    private Integer stockActual;

    @NotNull
    @Min(0)
    private Integer stockCritico;

    public VarianteRequest() {
    }

    public VarianteRequest(Integer productoId, String talla, String color, Integer stockActual,
                           Integer stockCritico) {
        this.productoId = productoId;
        this.talla = talla;
        this.color = color;
        this.stockActual = stockActual;
        this.stockCritico = stockCritico;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockCritico() {
        return stockCritico;
    }

    public void setStockCritico(Integer stockCritico) {
        this.stockCritico = stockCritico;
    }
}
