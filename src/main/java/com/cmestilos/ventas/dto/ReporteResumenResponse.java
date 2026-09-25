package com.cmestilos.ventas.dto;

import java.math.BigDecimal;

public class ReporteResumenResponse {
    private long cantidadVentas;
    private long ventasFisicas;
    private long ventasOnline;
    private long variantesStockCritico;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal impuesto = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    public ReporteResumenResponse() { }
    public ReporteResumenResponse(long cantidadVentas, long ventasFisicas, long ventasOnline,
                                  long variantesStockCritico, BigDecimal subtotal, BigDecimal impuesto,
                                  BigDecimal total) {
        this.cantidadVentas = cantidadVentas; this.ventasFisicas = ventasFisicas;
        this.ventasOnline = ventasOnline; this.variantesStockCritico = variantesStockCritico;
        this.subtotal = subtotal; this.impuesto = impuesto; this.total = total;
    }
    public long getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(long cantidadVentas) { this.cantidadVentas = cantidadVentas; }
    public long getVentasFisicas() { return ventasFisicas; }
    public void setVentasFisicas(long ventasFisicas) { this.ventasFisicas = ventasFisicas; }
    public long getVentasOnline() { return ventasOnline; }
    public void setVentasOnline(long ventasOnline) { this.ventasOnline = ventasOnline; }
    public long getVariantesStockCritico() { return variantesStockCritico; }
    public void setVariantesStockCritico(long variantesStockCritico) { this.variantesStockCritico = variantesStockCritico; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
