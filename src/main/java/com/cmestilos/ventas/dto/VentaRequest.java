package com.cmestilos.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class VentaRequest {
    @NotNull
    private Integer clienteId;

    @NotNull
    private String tipoVenta;

    private Integer metodoPagoId;

    @NotEmpty
    @Valid
    private List<VentaItemRequest> items = new ArrayList<>();

    public VentaRequest() {
    }

    public VentaRequest(Integer clienteId, String tipoVenta, Integer metodoPagoId,
                        List<VentaItemRequest> items) {
        this.clienteId = clienteId;
        this.tipoVenta = tipoVenta;
        this.metodoPagoId = metodoPagoId;
        this.items = items;
    }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }
    public Integer getMetodoPagoId() { return metodoPagoId; }
    public void setMetodoPagoId(Integer metodoPagoId) { this.metodoPagoId = metodoPagoId; }
    public List<VentaItemRequest> getItems() { return items; }
    public void setItems(List<VentaItemRequest> items) { this.items = items; }
}
