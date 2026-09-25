package com.cmestilos.ventas.dto;

public class ClienteResponse {
    private Integer idCliente;
    private String tipoDocumento;
    private String numDocumento;
    private String nombres;
    private String correo;
    private String telefono;

    public ClienteResponse() {
    }

    public ClienteResponse(Integer idCliente, String tipoDocumento, String numDocumento,
                           String nombres, String correo, String telefono) {
        this.idCliente = idCliente;
        this.tipoDocumento = tipoDocumento;
        this.numDocumento = numDocumento;
        this.nombres = nombres;
        this.correo = correo;
        this.telefono = telefono;
    }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) { this.numDocumento = numDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
