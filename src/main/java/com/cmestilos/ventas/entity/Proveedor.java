package com.cmestilos.ventas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proveedor")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;
    @Column(nullable = false, length = 15, unique = true)
    private String ruc;
    @Column(name = "razon_social", nullable = false, length = 120)
    private String razonSocial;
    @Column(length = 100)
    private String contacto;
    @Column(length = 15)
    private String telefono;
    @Column(length = 100)
    private String correo;
    @Column(length = 180)
    private String direccion;
    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "proveedor")
    private List<PedidoProveedor> pedidos = new ArrayList<>();

    public Proveedor() { }

    public Proveedor(Integer idProveedor, String ruc, String razonSocial, String contacto,
                     String telefono, String correo, String direccion, Boolean activo) {
        this.idProveedor = idProveedor;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.activo = activo;
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
    public List<PedidoProveedor> getPedidos() { return pedidos; }
    public void setPedidos(List<PedidoProveedor> pedidos) { this.pedidos = pedidos; }
}
