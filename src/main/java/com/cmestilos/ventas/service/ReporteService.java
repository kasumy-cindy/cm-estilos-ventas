package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.ReporteResumenResponse;
import com.cmestilos.ventas.entity.TipoVenta;
import com.cmestilos.ventas.entity.Venta;
import com.cmestilos.ventas.repository.VarianteProductoRepository;
import com.cmestilos.ventas.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final VarianteProductoRepository varianteRepository;

    public ReporteService(
            VentaRepository ventaRepository,
            VarianteProductoRepository varianteRepository) {

        this.ventaRepository = ventaRepository;
        this.varianteRepository = varianteRepository;
    }

    @Transactional(readOnly = true)
    public ReporteResumenResponse resumen(
            LocalDate desde,
            LocalDate hasta) {

        List<Venta> ventas = filtrar(desde, hasta);

        BigDecimal subtotal = ventas.stream()
                .map(venta -> venta.getSubtotal() == null
                        ? BigDecimal.ZERO
                        : venta.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal impuesto = ventas.stream()
                .map(venta -> venta.getImpuesto() == null
                        ? BigDecimal.ZERO
                        : venta.getImpuesto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = ventas.stream()
                .map(venta -> venta.getMontoTotal() == null
                        ? BigDecimal.ZERO
                        : venta.getMontoTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long fisicas = ventas.stream()
                .filter(venta -> venta.getTipoVenta() == TipoVenta.Física)
                .count();

        long online = ventas.stream()
                .filter(venta -> venta.getTipoVenta() == TipoVenta.Online)
                .count();

        return new ReporteResumenResponse(
                ventas.size(),
                fisicas,
                online,
                varianteRepository
                        .findVariantesEnStockCritico()
                        .size(),
                subtotal,
                impuesto,
                total
        );
    }

    @Transactional(readOnly = true)
    public List<Venta> ventas(
            LocalDate desde,
            LocalDate hasta) {

        return filtrar(desde, hasta);
    }

    @Transactional(readOnly = true)
    public List<Venta> ventasOnline(String estado) {

        String estadoFiltro = estado == null || estado.isBlank()
                ? null
                : estado.trim().toUpperCase();

        return ventaRepository
                .findAllByOrderByFechaHoraDesc()
                .stream()
                .filter(venta -> venta.getTipoVenta() == TipoVenta.Online)
                .filter(venta -> estadoFiltro == null
                        || estadoFiltro.equalsIgnoreCase(
                                venta.getEstadoPedido()
                        ))
                .toList();
    }

    private List<Venta> filtrar(
            LocalDate desde,
            LocalDate hasta) {

        LocalDateTime inicio =
                desde == null
                        ? null
                        : desde.atStartOfDay();

        LocalDateTime fin =
                hasta == null
                        ? null
                        : hasta.plusDays(1)
                                .atStartOfDay();

        return ventaRepository
                .findAllByOrderByFechaHoraDesc()
                .stream()
                .filter(venta -> venta.getFechaHora() != null)
                .filter(venta -> inicio == null
                        || !venta.getFechaHora()
                                .isBefore(inicio))
                .filter(venta -> fin == null
                        || venta.getFechaHora()
                                .isBefore(fin))
                .toList();
    }
}