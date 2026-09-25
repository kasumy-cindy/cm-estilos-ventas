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
import com.cmestilos.ventas.repository.DetalleVentaRepository;
import com.cmestilos.ventas.repository.UsuarioRepository;
import com.cmestilos.ventas.repository.VarianteProductoRepository;
import com.cmestilos.ventas.repository.VentaRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

    public VentaService(VentaRepository ventaRepository, DetalleVentaRepository detalleRepository,
                        VarianteProductoRepository varianteRepository, UsuarioRepository usuarioRepository,
                        ClienteService clienteService, MetodoPagoService metodoPagoService,
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
    public VentaResponse crear(VentaRequest request, String usernameAutenticado) {
        TipoVenta tipoVenta = TipoVenta.fromValor(request.getTipoVenta());
        Cliente cliente = clienteService.buscarEntidad(request.getClienteId());
        Usuario usuario = null;

        if (tipoVenta == TipoVenta.Física) {
            if (usernameAutenticado == null || usernameAutenticado.isBlank()) {
                throw new BusinessException("Una venta física requiere un usuario cajero autenticado");
            }
            usuario = usuarioRepository.findByUsername(usernameAutenticado)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        }

        Set<Integer> variantesRepetidas = new HashSet<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        List<VarianteProducto> variantes = new java.util.ArrayList<>();

        for (VentaItemRequest item : request.getItems()) {
            if (!variantesRepetidas.add(item.getVarianteId())) {
                throw new BusinessException("La variante " + item.getVarianteId()
                        + " está repetida en la venta");
            }
            VarianteProducto variante = varianteRepository.findById(item.getVarianteId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Variante no encontrada: " + item.getVarianteId()));
            variantes.add(variante);
            subtotal = subtotal.add(variante.getProducto().getPrecioVenta()
                    .multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        if (request.getMetodoPagoId() != null) {
            venta.setMetodoPago(metodoPagoService.buscarEntidad(request.getMetodoPagoId()));
        }
        venta.setFechaHora(LocalDateTime.now());
        venta.setTipoVenta(tipoVenta);
        venta.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        venta = ventaRepository.saveAndFlush(venta);
        entityManager.refresh(venta);

        for (int i = 0; i < request.getItems().size(); i++) {
            VentaItemRequest item = request.getItems().get(i);
            VarianteProducto variante = variantes.get(i);
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setVariante(variante);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnit(variante.getProducto().getPrecioVenta());
            detalleRepository.save(detalle);
        }
        detalleRepository.flush();

        entityManager.refresh(venta);
        return toResponse(venta, detalleRepository.findByVentaIdVenta(venta.getIdVenta()));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {
        return ventaRepository.findAllByOrderByFechaHoraDesc().stream()
                .map(venta -> toResponse(venta, detalleRepository.findByVentaIdVenta(venta.getIdVenta())))
                .toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse obtener(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + id));
        return toResponse(venta, detalleRepository.findByVentaIdVenta(id));
    }

    private VentaResponse toResponse(Venta venta, List<DetalleVenta> detalles) {
        List<DetalleVentaResponse> detalleResponses = detalles.stream().map(detalle -> {
            VarianteProducto variante = detalle.getVariante();
            BigDecimal importe = detalle.getPrecioUnit()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            return new DetalleVentaResponse(detalle.getIdDetalle(), variante.getIdVariante(),
                    variante.getProducto().getSku(), variante.getProducto().getNombre(),
                    variante.getTalla(), variante.getColor(), detalle.getCantidad(),
                    detalle.getPrecioUnit(), importe);
        }).toList();

        return new VentaResponse(venta.getIdVenta(), venta.getCliente().getIdCliente(),
                venta.getCliente().getNombres(), venta.getUsuario() == null ? null : venta.getUsuario().getIdUsuario(),
                venta.getUsuario() == null ? null : venta.getUsuario().getUsername(),
                venta.getMetodoPago() == null ? null : venta.getMetodoPago().getIdMetodoPago(),
                venta.getMetodoPago() == null ? null : venta.getMetodoPago().getNombre(), venta.getFechaHora(),
                venta.getTipoVenta().getValor(), venta.getSubtotal(), venta.getImpuesto(),
                venta.getMontoTotal(), detalleResponses);
    }
}
