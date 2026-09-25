package com.cmestilos.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class VentaRequest {

    @NotNull
    private Integer clienteId;

    @NotNull
    private String tipoVenta;

    private Integer metodoPagoId;

    @Size(max = 20)
    private String modalidadEntrega;

    @Size(max = 255)
    private String direccionEntrega;

    @Size(max = 30)
    private String telefonoEntrega;

    @NotEmpty
    @Valid
    private List<VentaItemRequest> items = new ArrayList<>();

    public VentaRequest() {
    }

    public VentaRequest(
            Integer clienteId,
            String tipoVenta,
            Integer metodoPagoId,
            List<VentaItemRequest> items) {

        this.clienteId = clienteId;
        this.tipoVenta = tipoVenta;
        this.metodoPagoId = metodoPagoId;
        this.items = items;
    }

    public VentaRequest(
            Integer clienteId,
            String tipoVenta,
            Integer metodoPagoId,
            String modalidadEntrega,
            String direccionEntrega,
            String telefonoEntrega,
            List<VentaItemRequest> items) {

        this.clienteId = clienteId;
        this.tipoVenta = tipoVenta;
        this.metodoPagoId = metodoPagoId;
        this.modalidadEntrega = modalidadEntrega;
        this.direccionEntrega = direccionEntrega;
        this.telefonoEntrega = telefonoEntrega;
        this.items = items;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public Integer getMetodoPagoId() {
        return metodoPagoId;
    }

    public void setMetodoPagoId(Integer metodoPagoId) {
        this.metodoPagoId = metodoPagoId;
    }

    public String getModalidadEntrega() {
        return modalidadEntrega;
    }

    public void setModalidadEntrega(String modalidadEntrega) {
        this.modalidadEntrega = modalidadEntrega;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getTelefonoEntrega() {
        return telefonoEntrega;
    }

    public void setTelefonoEntrega(String telefonoEntrega) {
        this.telefonoEntrega = telefonoEntrega;
    }

    public List<VentaItemRequest> getItems() {
        return items;
    }

    public void setItems(List<VentaItemRequest> items) {
        this.items = items;
    }
}