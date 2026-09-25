package com.cmestilos.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoProveedorResponse {
    private Integer idPedido;
    private Integer proveedorId;
    private String proveedor;
    private LocalDateTime fechaSolicitud;
    private String estado;
    private String observacion;
    private BigDecimal total;
    private List<DetallePedidoResponse> detalles = new ArrayList<>();

    public PedidoProveedorResponse() { }
    public PedidoProveedorResponse(Integer idPedido, Integer proveedorId, String proveedor,
                                   LocalDateTime fechaSolicitud, String estado, String observacion,
                                   BigDecimal total, List<DetallePedidoResponse> detalles) {
        this.idPedido = idPedido; this.proveedorId = proveedorId; this.proveedor = proveedor;
        this.fechaSolicitud = fechaSolicitud; this.estado = estado; this.observacion = observacion;
        this.total = total; this.detalles = detalles;
    }
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<DetallePedidoResponse> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoResponse> detalles) { this.detalles = detalles; }
}
