package com.cmestilos.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class PedidoProveedorRequest {
    @NotNull
    private Integer proveedorId;
    @Size(max = 250)
    private String observacion;
    @NotEmpty @Valid
    private List<PedidoItemRequest> items = new ArrayList<>();

    public PedidoProveedorRequest() { }
    public PedidoProveedorRequest(Integer proveedorId, String observacion, List<PedidoItemRequest> items) {
        this.proveedorId = proveedorId; this.observacion = observacion; this.items = items;
    }
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public List<PedidoItemRequest> getItems() { return items; }
    public void setItems(List<PedidoItemRequest> items) { this.items = items; }
}
