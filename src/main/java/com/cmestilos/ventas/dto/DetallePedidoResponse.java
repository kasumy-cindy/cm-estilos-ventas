package com.cmestilos.ventas.dto;

import java.math.BigDecimal;

public class DetallePedidoResponse {
    private Integer idDetallePedido;
    private Integer varianteId;
    private String producto;
    private String talla;
    private String color;
    private Integer cantidad;
    private BigDecimal precioUnit;
    private BigDecimal importe;

    public DetallePedidoResponse() { }
    public DetallePedidoResponse(Integer idDetallePedido, Integer varianteId, String producto,
                                 String talla, String color, Integer cantidad, BigDecimal precioUnit,
                                 BigDecimal importe) {
        this.idDetallePedido = idDetallePedido; this.varianteId = varianteId; this.producto = producto;
        this.talla = talla; this.color = color; this.cantidad = cantidad; this.precioUnit = precioUnit;
        this.importe = importe;
    }
    public Integer getIdDetallePedido() { return idDetallePedido; }
    public void setIdDetallePedido(Integer idDetallePedido) { this.idDetallePedido = idDetallePedido; }
    public Integer getVarianteId() { return varianteId; }
    public void setVarianteId(Integer varianteId) { this.varianteId = varianteId; }
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
