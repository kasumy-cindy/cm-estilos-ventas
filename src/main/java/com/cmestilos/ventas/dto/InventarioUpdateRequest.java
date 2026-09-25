package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventarioUpdateRequest {
    @NotNull
    @Min(0)
    private Integer stockActual;

    @NotNull
    @Min(0)
    private Integer stockCritico;

    public InventarioUpdateRequest() {
    }

    public InventarioUpdateRequest(Integer stockActual, Integer stockCritico) {
        this.stockActual = stockActual;
        this.stockCritico = stockCritico;
    }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Integer getStockCritico() { return stockCritico; }
    public void setStockCritico(Integer stockCritico) { this.stockCritico = stockCritico; }
}
