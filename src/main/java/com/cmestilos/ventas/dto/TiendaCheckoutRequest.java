package com.cmestilos.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class TiendaCheckoutRequest {

    @NotBlank
    @Size(max = 10)
    private String tipoDocumento;

    @NotBlank
    @Size(max = 15)
    private String numDocumento;

    @NotBlank
    @Size(max = 100)
    private String nombres;

    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @Size(max = 15)
    private String telefono;

    private Integer metodoPagoId;

    @Size(max = 20)
    private String modalidadEntrega = "RECOJO_TIENDA";

    @Size(max = 255)
    private String direccionEntrega;

    @NotEmpty
    @Valid
    private List<VentaItemRequest> items = new ArrayList<>();

    public TiendaCheckoutRequest() {
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public List<VentaItemRequest> getItems() {
        return items;
    }

    public void setItems(List<VentaItemRequest> items) {
        this.items = items;
    }
}