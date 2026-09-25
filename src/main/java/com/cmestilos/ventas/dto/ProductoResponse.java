package com.cmestilos.ventas.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoResponse {
    private Integer idProducto;
    private Integer categoriaId;
    private String categoriaNombre;
    private String sku;
    private String nombre;
    private BigDecimal precioVenta;
    private String imagenUrl;
    private Boolean activo;
    private List<VarianteResponse> variantes = new ArrayList<>();

    public ProductoResponse() {
    }

    public ProductoResponse(Integer idProducto, Integer categoriaId, String categoriaNombre,
                            String sku, String nombre, BigDecimal precioVenta, String imagenUrl, Boolean activo,
                            List<VarianteResponse> variantes) {
        this.idProducto = idProducto;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.sku = sku;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
        this.variantes = variantes;
    }

    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }
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
    public List<VarianteResponse> getVariantes() { return variantes; }
    public void setVariantes(List<VarianteResponse> variantes) { this.variantes = variantes; }
}
