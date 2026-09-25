package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductoRequest {
    @NotNull
    private Integer categoriaId;

    @NotBlank
    @Size(max = 20)
    private String sku;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal precioVenta;

    @Size(max = 500)
    private String imagenUrl;

    private Boolean activo = true;

    public ProductoRequest() {
    }

    public ProductoRequest(Integer categoriaId, String sku, String nombre, BigDecimal precioVenta,
                           Boolean activo) {
        this.categoriaId = categoriaId;
        this.sku = sku;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.activo = activo;
    }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
