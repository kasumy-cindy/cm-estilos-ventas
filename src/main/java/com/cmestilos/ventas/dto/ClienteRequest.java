package com.cmestilos.ventas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClienteRequest {
    @NotBlank
    @Size(max = 10)
    private String tipoDocumento;

    @NotBlank
    @Size(max = 15)
    private String numDocumento;

    @NotBlank
    @Size(max = 100)
    private String nombres;

    @Email
    @Size(max = 100)
    private String correo;

    @Size(max = 15)
    private String telefono;

    public ClienteRequest() {
    }

    public ClienteRequest(String tipoDocumento, String numDocumento, String nombres,
                          String correo, String telefono) {
        this.tipoDocumento = tipoDocumento;
        this.numDocumento = numDocumento;
        this.nombres = nombres;
        this.correo = correo;
        this.telefono = telefono;
    }

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
