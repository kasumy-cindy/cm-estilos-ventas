package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PedidoItemRequest {
    @NotNull
    private Integer varianteId;
    @NotNull @Min(1)
    private Integer cantidad;
    @NotNull @DecimalMin("0.01")
    private BigDecimal precioUnit;

    public PedidoItemRequest() { }
    public PedidoItemRequest(Integer varianteId, Integer cantidad, BigDecimal precioUnit) {
        this.varianteId = varianteId; this.cantidad = cantidad; this.precioUnit = precioUnit;
    }
    public Integer getVarianteId() { return varianteId; }
    public void setVarianteId(Integer varianteId) { this.varianteId = varianteId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }
}
