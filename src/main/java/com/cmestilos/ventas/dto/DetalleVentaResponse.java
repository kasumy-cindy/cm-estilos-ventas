package com.cmestilos.ventas.dto;

import java.math.BigDecimal;

public class DetalleVentaResponse {
    private Integer idDetalle;
    private Integer varianteId;
    private String sku;
    private String producto;
    private String talla;
    private String color;
    private Integer cantidad;
    private BigDecimal precioUnit;
    private BigDecimal importe;

    public DetalleVentaResponse() {
    }

    public DetalleVentaResponse(Integer idDetalle, Integer varianteId, String sku, String producto,
                                String talla, String color, Integer cantidad, BigDecimal precioUnit,
                                BigDecimal importe) {
        this.idDetalle = idDetalle;
        this.varianteId = varianteId;
        this.sku = sku;
        this.producto = producto;
        this.talla = talla;
        this.color = color;
        this.cantidad = cantidad;
        this.precioUnit = precioUnit;
        this.importe = importe;
    }

    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }
    public Integer getVarianteId() { return varianteId; }
    public void setVarianteId(Integer varianteId) { this.varianteId = varianteId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
}
