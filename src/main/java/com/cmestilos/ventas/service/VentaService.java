package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.DetalleVentaResponse;
import com.cmestilos.ventas.dto.VentaItemRequest;
import com.cmestilos.ventas.dto.VentaRequest;
import com.cmestilos.ventas.dto.VentaResponse;
import com.cmestilos.ventas.entity.Cliente;
import com.cmestilos.ventas.entity.DetalleVenta;
import com.cmestilos.ventas.entity.TipoVenta;
import com.cmestilos.ventas.entity.Usuario;
import com.cmestilos.ventas.entity.Venta;
import com.cmestilos.ventas.entity.VarianteProducto;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.ClienteRepository;
import com.cmestilos.ventas.repository.DetalleVentaRepository;
import com.cmestilos.ventas.repository.UsuarioRepository;
import com.cmestilos.ventas.repository.VarianteProductoRepository;
import com.cmestilos.ventas.repository.VentaRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleRepository;
    private final VarianteProductoRepository varianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteService clienteService;
    private final MetodoPagoService metodoPagoService;
    private final EntityManager entityManager;

    public VentaService(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleRepository,
            VarianteProductoRepository varianteRepository,
            UsuarioRepository usuarioRepository,
            ClienteService clienteService,
            MetodoPagoService metodoPagoService,
            EntityManager entityManager) {

        this.ventaRepository = ventaRepository;
        this.detalleRepository = detalleRepository;
        this.varianteRepository = varianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteService = clienteService;
        this.metodoPagoService = metodoPagoService;
        this.entityManager = entityManager;
    }

    @Transactional
    public VentaResponse crear(
            VentaRequest request,
            String usernameAutenticado) {

        if (request == null) {
            throw new BusinessException(
                    "La solicitud de venta es obligatoria"
            );
        }

        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new BusinessException(
                    "La venta debe tener al menos un producto"
            );
        }

        TipoVenta tipoVenta =
                TipoVenta.fromValor(request.getTipoVenta());

        Cliente cliente =
                clienteService.buscarEntidad(request.getClienteId());

        Usuario usuario = null;

        boolean esOnline =
                tipoVenta == TipoVenta.Online;

        if (!esOnline) {

            if (usernameAutenticado == null
                    || usernameAutenticado.isBlank()) {

                throw new BusinessException(
                        "Una venta física requiere un usuario autenticado"
                );
            }

            usuario = usuarioRepository
                    .findByUsername(usernameAutenticado)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Usuario autenticado no encontrado"
                            )
                    );
        }

        Set<Integer> variantesRepetidas =
                new HashSet<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        List<VarianteProducto> variantes =
                new ArrayList<>();

        for (VentaItemRequest item : request.getItems()) {

            if (item == null
                    || item.getVarianteId() == null) {

                throw new BusinessException(
                        "La variante del producto es obligatoria"
                );
            }

            if (item.getCantidad() == null
                    || item.getCantidad() <= 0) {

                throw new BusinessException(
                        "La cantidad debe ser mayor que cero"
                );
            }

            if (!variantesRepetidas.add(
                    item.getVarianteId())) {

                throw new BusinessException(
                        "La variante "
                                + item.getVarianteId()
                                + " está repetida en la venta"
                );
            }

            VarianteProducto variante =
                    varianteRepository
                            .findById(item.getVarianteId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Variante no encontrada: "
                                                    + item.getVarianteId()
                                    )
                            );

            if (variante.getProducto() == null
                    || variante.getProducto()
                            .getPrecioVenta() == null) {

                throw new BusinessException(
                        "El producto no tiene un precio válido"
                );
            }

            variantes.add(variante);

            BigDecimal importe =
                    variante.getProducto()
                            .getPrecioVenta()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getCantidad()
                                    )
                            );

            subtotal = subtotal.add(importe);
        }

        subtotal = subtotal.setScale(
                2,
                RoundingMode.HALF_UP
        );

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "El monto total debe ser mayor que cero"
            );
        }

        Venta venta = new Venta();

        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFechaHora(LocalDateTime.now());
        venta.setTipoVenta(tipoVenta);
        venta.setSubtotal(subtotal);

        if (request.getMetodoPagoId() != null) {
            venta.setMetodoPago(
                    metodoPagoService.buscarEntidad(
                            request.getMetodoPagoId()
                    )
            );
        }

        if (esOnline) {
            venta.setEstadoPedido("PENDIENTE");
            venta.setEstadoPago("PENDIENTE");
            venta.setModalidadEntrega(
                    normalizarModalidad(
                            request.getModalidadEntrega()
                    )
            );
            venta.setDireccionEntrega(
                    request.getDireccionEntrega()
            );
            venta.setTelefonoEntrega(
                    request.getTelefonoEntrega()
            );
        } else {
            venta.setEstadoPedido("ENTREGADO");
            venta.setEstadoPago("PAGADO");
            venta.setModalidadEntrega("TIENDA");
            venta.setDireccionEntrega(null);
            venta.setTelefonoEntrega(null);
        }

        venta = ventaRepository.saveAndFlush(venta);

        entityManager.refresh(venta);

        for (int i = 0; i < request.getItems().size(); i++) {

            VentaItemRequest item =
                    request.getItems().get(i);

            VarianteProducto variante =
                    variantes.get(i);

            DetalleVenta detalle =
                    new DetalleVenta();

            detalle.setVenta(venta);
            detalle.setVariante(variante);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnit(
                    variante.getProducto()
                            .getPrecioVenta()
            );

            detalleRepository.save(detalle);
        }

        detalleRepository.flush();

        entityManager.refresh(venta);

        List<DetalleVenta> detalles =
                detalleRepository.findByVentaIdVenta(
                        venta.getIdVenta()
                );

        return toResponse(venta, detalles);
    }

    @Transactional
    public VentaResponse actualizarEstado(
            Integer id,
            String estado) {

        if (estado == null || estado.isBlank()) {
            throw new BusinessException(
                    "El estado del pedido es obligatorio"
            );
        }

        String estadoNormalizado =
                estado.trim().toUpperCase();

        Set<String> estadosPermitidos = Set.of(
                "PENDIENTE",
                "CONFIRMADO",
                "PREPARANDO",
                "LISTO_PARA_RECOGER",
                "ENVIADO",
                "ENTREGADO",
                "CANCELADO"
        );

        if (!estadosPermitidos.contains(estadoNormalizado)) {
            throw new BusinessException(
                    "Estado de pedido no válido"
            );
        }

        Venta venta =
                ventaRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Venta no encontrada: " + id
                                )
                        );

        if (venta.getTipoVenta() != TipoVenta.Online) {
            throw new BusinessException(
                    "Solo se puede cambiar el estado de pedidos online"
            );
        }

        if ("ENTREGADO".equals(venta.getEstadoPedido())
                || "CANCELADO".equals(venta.getEstadoPedido())) {

            throw new BusinessException(
                    "El pedido ya no puede cambiar de estado"
            );
        }

        venta.setEstadoPedido(estadoNormalizado);

        if ("CANCELADO".equals(estadoNormalizado)) {
            venta.setEstadoPago("CANCELADO");
        }

        if ("ENTREGADO".equals(estadoNormalizado)) {
            venta.setEstadoPago("PAGADO");
        }

        venta = ventaRepository.saveAndFlush(venta);

        List<DetalleVenta> detalles =
                detalleRepository.findByVentaIdVenta(id);

        return toResponse(venta, detalles);
    }

    @Transactional(readOnly = true)
    public VentaResponse consultarPorCliente(
            Integer id,
            String correo) {

        Venta venta =
                buscarVenta(id);

        validarCorreo(venta, correo);

        List<DetalleVenta> detalles =
                detalleRepository.findByVentaIdVenta(id);

        return toResponse(venta, detalles);
    }

    @Transactional
    public VentaResponse cancelarPorCliente(
            Integer id,
            String correo) {

        Venta venta =
                buscarVenta(id);

        validarCorreo(venta, correo);

        if (venta.getTipoVenta() != TipoVenta.Online) {
            throw new BusinessException(
                    "Solo se pueden cancelar pedidos online"
            );
        }

        if (!"PENDIENTE".equals(venta.getEstadoPedido())
                && !"CONFIRMADO".equals(
                        venta.getEstadoPedido())) {

            throw new BusinessException(
                    "El pedido ya está siendo preparado o enviado"
            );
        }

        venta.setEstadoPedido("CANCELADO");
        venta.setEstadoPago("CANCELADO");

        venta = ventaRepository.saveAndFlush(venta);

        List<DetalleVenta> detalles =
                detalleRepository.findByVentaIdVenta(id);

        return toResponse(venta, detalles);
    }

    private void validarCorreo(
            Venta venta,
            String correo) {

        if (correo == null
                || correo.isBlank()
                || venta.getCliente() == null
                || venta.getCliente().getCorreo() == null
                || !venta.getCliente()
                        .getCorreo()
                        .equalsIgnoreCase(
                                correo.trim()
                        )) {

            throw new BusinessException(
                    "El correo no coincide con el pedido"
            );
        }
    }

    private Venta buscarVenta(Integer id) {

        return ventaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venta no encontrada: " + id
                        )
                );
    }

    private String normalizarModalidad(
            String modalidad) {

        if (modalidad == null
                || modalidad.isBlank()) {

            return "RECOJO_TIENDA";
        }

        String valor =
                modalidad.trim().toUpperCase();

        if (!valor.equals("RECOJO_TIENDA")
                && !valor.equals("DELIVERY")) {

            throw new BusinessException(
                    "Modalidad de entrega no válida"
            );
        }

        return valor;
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {

        return ventaRepository
                .findAllByOrderByFechaHoraDesc()
                .stream()
                .map(venta -> {

                    List<DetalleVenta> detalles =
                            detalleRepository
                                    .findByVentaIdVenta(
                                            venta.getIdVenta()
                                    );

                    return toResponse(
                            venta,
                            detalles
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse obtener(Integer id) {

        Venta venta =
                buscarVenta(id);

        List<DetalleVenta> detalles =
                detalleRepository.findByVentaIdVenta(id);

        return toResponse(venta, detalles);
    }

    private VentaResponse toResponse(
            Venta venta,
            List<DetalleVenta> detalles) {

        List<DetalleVentaResponse>
                detalleResponses =
                detalles.stream()
                        .map(detalle -> {

                            VarianteProducto variante =
                                    detalle.getVariante();

                            BigDecimal precioUnitario =
                                    detalle.getPrecioUnit()
                                            == null
                                            ? BigDecimal.ZERO
                                            : detalle.getPrecioUnit();

                            BigDecimal importe =
                                    precioUnitario.multiply(
                                            BigDecimal.valueOf(
                                                    detalle.getCantidad()
                                            )
                                    ).setScale(
                                            2,
                                            RoundingMode.HALF_UP
                                    );

                            return new DetalleVentaResponse(
                                    detalle.getIdDetalle(),
                                    variante == null
                                            ? null
                                            : variante.getIdVariante(),
                                    variante == null
                                            || variante.getProducto()
                                                    == null
                                            ? null
                                            : variante.getProducto()
                                                    .getSku(),
                                    variante == null
                                            || variante.getProducto()
                                                    == null
                                            ? null
                                            : variante.getProducto()
                                                    .getNombre(),
                                    variante == null
                                            ? null
                                            : variante.getTalla(),
                                    variante == null
                                            ? null
                                            : variante.getColor(),
                                    detalle.getCantidad(),
                                    precioUnitario,
                                    importe
                            );
                        })
                        .toList();

        return new VentaResponse(
                venta.getIdVenta(),

                venta.getCliente() == null
                        ? null
                        : venta.getCliente().getIdCliente(),

                venta.getCliente() == null
                        ? null
                        : venta.getCliente().getNombres(),

                venta.getUsuario() == null
                        ? null
                        : venta.getUsuario().getIdUsuario(),

                venta.getUsuario() == null
                        ? null
                        : venta.getUsuario().getUsername(),

                venta.getMetodoPago() == null
                        ? null
                        : venta.getMetodoPago()
                                .getIdMetodoPago(),

                venta.getMetodoPago() == null
                        ? null
                        : venta.getMetodoPago().getNombre(),

                venta.getFechaHora(),

                venta.getTipoVenta() == null
                        ? null
                        : venta.getTipoVenta().getValor(),

                venta.getSubtotal(),
                venta.getImpuesto(),
                venta.getMontoTotal(),
                venta.getEstadoPedido(),
                venta.getEstadoPago(),
                venta.getModalidadEntrega(),
                venta.getDireccionEntrega(),
                venta.getTelefonoEntrega(),
                detalleResponses
        );
    }
}