package com.cmestilos.ventas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variante", nullable = false)
    private VarianteProducto variante;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnit;

    public DetalleVenta() {
    }

    public DetalleVenta(Integer idDetalle, Venta venta, VarianteProducto variante,
                        Integer cantidad, BigDecimal precioUnit) {
        this.idDetalle = idDetalle;
        this.venta = venta;
        this.variante = variante;
        this.cantidad = cantidad;
        this.precioUnit = precioUnit;
    }

    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public VarianteProducto getVariante() {
        return variante;
    }

    public void setVariante(VarianteProducto variante) {
        this.variante = variante;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnit() {
        return precioUnit;
    }

    public void setPrecioUnit(BigDecimal precioUnit) {
        this.precioUnit = precioUnit;
    }
}
