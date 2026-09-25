package com.cmestilos.ventas.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago")
    private MetodoPago metodoPago;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_venta", nullable = false, columnDefinition = "tipo_venta")
    private TipoVenta tipoVenta;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "impuesto", precision = 10, scale = 2)
    private BigDecimal impuesto;

    @Column(name = "monto_total", precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "estado_pedido", length = 30)
    private String estadoPedido;

    @Column(name = "estado_pago", length = 30)
    private String estadoPago;

    @Column(name = "modalidad_entrega", length = 20)
    private String modalidadEntrega;

    @Column(name = "direccion_entrega", length = 255)
    private String direccionEntrega;

    @Column(name = "telefono_entrega", length = 30)
    private String telefonoEntrega;

    @OneToMany(
            mappedBy = "venta",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
        this.estadoPedido = "ENTREGADO";
        this.estadoPago = "PAGADO";
        this.modalidadEntrega = "TIENDA";
    }

    public Venta(
            Integer idVenta,
            Cliente cliente,
            Usuario usuario,
            LocalDateTime fechaHora,
            TipoVenta tipoVenta,
            BigDecimal subtotal,
            BigDecimal impuesto,
            BigDecimal montoTotal) {

        this();

        this.idVenta = idVenta;
        this.cliente = cliente;
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.tipoVenta = tipoVenta;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.montoTotal = montoTotal;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoVenta getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(TipoVenta tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
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

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}