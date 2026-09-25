package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class VentaItemRequest {
    @NotNull
    private Integer varianteId;

    @NotNull
    @Min(1)
    private Integer cantidad;

    public VentaItemRequest() {
    }

    public VentaItemRequest(Integer varianteId, Integer cantidad) {
        this.varianteId = varianteId;
        this.cantidad = cantidad;
    }

    public Integer getVarianteId() { return varianteId; }
    public void setVarianteId(Integer varianteId) { this.varianteId = varianteId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
