package com.cmestilos.ventas.dto;

public class ProveedorResponse {
    private Integer idProveedor;
    private String ruc;
    private String razonSocial;
    private String contacto;
    private String telefono;
    private String correo;
    private String direccion;
    private Boolean activo;

    public ProveedorResponse() { }
    public ProveedorResponse(Integer idProveedor, String ruc, String razonSocial, String contacto,
                             String telefono, String correo, String direccion, Boolean activo) {
        this.idProveedor = idProveedor; this.ruc = ruc; this.razonSocial = razonSocial;
        this.contacto = contacto; this.telefono = telefono; this.correo = correo;
        this.direccion = direccion; this.activo = activo;
    }
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }
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
