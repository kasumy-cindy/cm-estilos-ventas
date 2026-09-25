package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProveedorRequest {
    @NotBlank @Size(max = 15)
    private String ruc;
    @NotBlank @Size(max = 120)
    private String razonSocial;
    @Size(max = 100)
    private String contacto;
    @Size(max = 15)
    private String telefono;
    @Size(max = 100)
    private String correo;
    @Size(max = 180)
    private String direccion;
    private Boolean activo = true;

    public ProveedorRequest() { }
    public ProveedorRequest(String ruc, String razonSocial, String contacto, String telefono,
                            String correo, String direccion, Boolean activo) {
        this.ruc = ruc; this.razonSocial = razonSocial; this.contacto = contacto;
        this.telefono = telefono; this.correo = correo; this.direccion = direccion; this.activo = activo;
    }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
