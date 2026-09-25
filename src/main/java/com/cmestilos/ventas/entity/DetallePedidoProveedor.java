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
@Table(name = "detalle_pedido_proveedor")
public class DetallePedidoProveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido")
    private Integer idDetallePedido;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private PedidoProveedor pedido;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variante", nullable = false)
    private VarianteProducto variante;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnit;

    public DetallePedidoProveedor() { }

    public DetallePedidoProveedor(Integer idDetallePedido, PedidoProveedor pedido,
                                  VarianteProducto variante, Integer cantidad, BigDecimal precioUnit) {
        this.idDetallePedido = idDetallePedido;
        this.pedido = pedido;
        this.variante = variante;
        this.cantidad = cantidad;
        this.precioUnit = precioUnit;
    }

    public Integer getIdDetallePedido() { return idDetallePedido; }
    public void setIdDetallePedido(Integer idDetallePedido) { this.idDetallePedido = idDetallePedido; }
    public PedidoProveedor getPedido() { return pedido; }
    public void setPedido(PedidoProveedor pedido) { this.pedido = pedido; }
    public VarianteProducto getVariante() { return variante; }
    public void setVariante(VarianteProducto variante) { this.variante = variante; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }
}
