package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.DetallePedidoResponse;
import com.cmestilos.ventas.dto.PedidoItemRequest;
import com.cmestilos.ventas.dto.PedidoProveedorRequest;
import com.cmestilos.ventas.dto.PedidoProveedorResponse;
import com.cmestilos.ventas.entity.DetallePedidoProveedor;
import com.cmestilos.ventas.entity.PedidoProveedor;
import com.cmestilos.ventas.entity.Proveedor;
import com.cmestilos.ventas.entity.VarianteProducto;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.DetallePedidoProveedorRepository;
import com.cmestilos.ventas.repository.PedidoProveedorRepository;
import com.cmestilos.ventas.repository.VarianteProductoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoProveedorService {
    private final PedidoProveedorRepository pedidoRepository;
    private final DetallePedidoProveedorRepository detalleRepository;
    private final VarianteProductoRepository varianteRepository;
    private final ProveedorService proveedorService;

    public PedidoProveedorService(PedidoProveedorRepository pedidoRepository,
                                  DetallePedidoProveedorRepository detalleRepository,
                                  VarianteProductoRepository varianteRepository,
                                  ProveedorService proveedorService) {
        this.pedidoRepository = pedidoRepository; this.detalleRepository = detalleRepository;
        this.varianteRepository = varianteRepository; this.proveedorService = proveedorService;
    }

    @Transactional
    public PedidoProveedorResponse crear(PedidoProveedorRequest request) {
        Proveedor proveedor = proveedorService.buscarEntidad(request.getProveedorId());
        if (!Boolean.TRUE.equals(proveedor.getActivo())) throw new BusinessException("El proveedor está inactivo");
        Set<Integer> ids = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        PedidoProveedor pedido = new PedidoProveedor();
        pedido.setProveedor(proveedor); pedido.setFechaSolicitud(LocalDateTime.now());
        pedido.setEstado("PENDIENTE"); pedido.setObservacion(request.getObservacion());
        pedido.setTotal(BigDecimal.ZERO);
        pedido = pedidoRepository.saveAndFlush(pedido);
        for (PedidoItemRequest item : request.getItems()) {
            if (!ids.add(item.getVarianteId())) throw new BusinessException("La variante está repetida");
            VarianteProducto variante = varianteRepository.findById(item.getVarianteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada: " + item.getVarianteId()));
            DetallePedidoProveedor detalle = new DetallePedidoProveedor();
            detalle.setPedido(pedido); detalle.setVariante(variante); detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnit(item.getPrecioUnit()); detalleRepository.save(detalle);
            total = total.add(item.getPrecioUnit().multiply(BigDecimal.valueOf(item.getCantidad())));
        }
        pedido.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        pedidoRepository.saveAndFlush(pedido);
        return toResponse(pedido, detalleRepository.findByPedidoIdPedido(pedido.getIdPedido()));
    }

    @Transactional(readOnly = true)
    public List<PedidoProveedorResponse> listar() {
        return pedidoRepository.findAllByOrderByFechaSolicitudDesc().stream()
                .map(p -> toResponse(p, detalleRepository.findByPedidoIdPedido(p.getIdPedido()))).toList();
    }

    @Transactional
    public PedidoProveedorResponse cambiarEstado(Integer id, String nuevoEstado) {
        PedidoProveedor pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
        String estado = nuevoEstado.toUpperCase();
        if (!Set.of("PENDIENTE", "RECIBIDO", "CANCELADO").contains(estado)) {
            throw new BusinessException("Estado no válido");
        }
        if ("RECIBIDO".equals(estado) && !"RECIBIDO".equals(pedido.getEstado())) {
            for (DetallePedidoProveedor d : detalleRepository.findByPedidoIdPedido(id)) {
                VarianteProducto v = d.getVariante();
                v.setStockActual(v.getStockActual() + d.getCantidad());
                varianteRepository.save(v);
            }
        }
        pedido.setEstado(estado);
        return toResponse(pedidoRepository.save(pedido), detalleRepository.findByPedidoIdPedido(id));
    }

    private PedidoProveedorResponse toResponse(PedidoProveedor p, List<DetallePedidoProveedor> detalles) {
        List<DetallePedidoResponse> rows = detalles.stream().map(d -> {
            VarianteProducto v = d.getVariante();
            BigDecimal importe = d.getPrecioUnit().multiply(BigDecimal.valueOf(d.getCantidad()));
            return new DetallePedidoResponse(d.getIdDetallePedido(), v.getIdVariante(), v.getProducto().getNombre(),
                    v.getTalla(), v.getColor(), d.getCantidad(), d.getPrecioUnit(), importe);
        }).toList();
        return new PedidoProveedorResponse(p.getIdPedido(), p.getProveedor().getIdProveedor(),
                p.getProveedor().getRazonSocial(), p.getFechaSolicitud(), p.getEstado(), p.getObservacion(),
                p.getTotal(), rows);
    }
}
