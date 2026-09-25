package com.cmestilos.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaResponse {
    private Integer idVenta;
    private Integer clienteId;
    private String cliente;
    private Integer usuarioId;
    private String usuario;
    private Integer metodoPagoId;
    private String metodoPago;
    private LocalDateTime fechaHora;
    private String tipoVenta;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal montoTotal;
    private List<DetalleVentaResponse> detalles = new ArrayList<>();

    public VentaResponse() {
    }

    public VentaResponse(Integer idVenta, Integer clienteId, String cliente, Integer usuarioId,
                         String usuario, Integer metodoPagoId, String metodoPago,
                         LocalDateTime fechaHora, String tipoVenta,
                         BigDecimal subtotal, BigDecimal impuesto, BigDecimal montoTotal,
                         List<DetalleVentaResponse> detalles) {
        this.idVenta = idVenta;
        this.clienteId = clienteId;
        this.cliente = cliente;
        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.metodoPagoId = metodoPagoId;
        this.metodoPago = metodoPago;
        this.fechaHora = fechaHora;
        this.tipoVenta = tipoVenta;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.montoTotal = montoTotal;
        this.detalles = detalles;
    }

    public Integer getIdVenta() { return idVenta; }
    public void setIdVenta(Integer idVenta) { this.idVenta = idVenta; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public Integer getMetodoPagoId() { return metodoPagoId; }
    public void setMetodoPagoId(Integer metodoPagoId) { this.metodoPagoId = metodoPagoId; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public List<DetalleVentaResponse> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaResponse> detalles) { this.detalles = detalles; }
}
