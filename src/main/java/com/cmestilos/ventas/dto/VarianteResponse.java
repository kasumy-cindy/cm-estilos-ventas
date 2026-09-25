package com.cmestilos.ventas.dto;

import java.math.BigDecimal;

public class VarianteResponse {
    private Integer idVariante;
    private Integer productoId;
    private String sku;
    private String productoNombre;
    private String talla;
    private String color;
    private BigDecimal precioVenta;
    private String imagenUrl;
    private Integer stockActual;
    private Integer stockCritico;
    private boolean stockCriticoAlcanzado;

    public VarianteResponse() {
    }

    public VarianteResponse(Integer idVariante, Integer productoId, String sku, String productoNombre,
                            String talla, String color, BigDecimal precioVenta, String imagenUrl, Integer stockActual, Integer stockCritico,
                            boolean stockCriticoAlcanzado) {
        this.idVariante = idVariante;
        this.productoId = productoId;
        this.sku = sku;
        this.productoNombre = productoNombre;
        this.talla = talla;
        this.color = color;
        this.precioVenta = precioVenta;
        this.imagenUrl = imagenUrl;
        this.stockActual = stockActual;
        this.stockCritico = stockCritico;
        this.stockCriticoAlcanzado = stockCriticoAlcanzado;
    }

    public Integer getIdVariante() { return idVariante; }
    public void setIdVariante(Integer idVariante) { this.idVariante = idVariante; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Integer getStockCritico() { return stockCritico; }
    public void setStockCritico(Integer stockCritico) { this.stockCritico = stockCritico; }
    public boolean isStockCriticoAlcanzado() { return stockCriticoAlcanzado; }
    public void setStockCriticoAlcanzado(boolean stockCriticoAlcanzado) { this.stockCriticoAlcanzado = stockCriticoAlcanzado; }
}
