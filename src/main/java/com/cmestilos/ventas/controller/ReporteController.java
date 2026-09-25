package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.DetalleVentaResponse;
import com.cmestilos.ventas.dto.ReporteResumenResponse;
import com.cmestilos.ventas.dto.VentaResponse;
import com.cmestilos.ventas.entity.DetalleVenta;
import com.cmestilos.ventas.entity.Venta;
import com.cmestilos.ventas.repository.DetalleVentaRepository;
import com.cmestilos.ventas.service.ReporteService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Transactional(readOnly = true)
public class ReporteController {

    private final ReporteService reporteService;
    private final DetalleVentaRepository detalleRepository;

    public ReporteController(
            ReporteService reporteService,
            DetalleVentaRepository detalleRepository) {

        this.reporteService = reporteService;
        this.detalleRepository = detalleRepository;
    }

    @GetMapping("/resumen")
    public ResponseEntity<ReporteResumenResponse> resumen(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {

        ReporteResumenResponse resultado =
                reporteService.resumen(desde, hasta);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<VentaResponse>> ventas(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {

        List<VentaResponse> resultado = reporteService
                .ventas(desde, hasta)
                .stream()
                .map(this::convertirVenta)
                .toList();

        return ResponseEntity.ok(resultado);
    }

    @GetMapping(
            value = "/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generarPdf(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta)
            throws Exception {

        List<Venta> ventas =
                reporteService.ventas(desde, hasta);

        ByteArrayOutputStream salida =
                new ByteArrayOutputStream();

        Document documento =
                new Document(PageSize.A4.rotate());

        PdfWriter.getInstance(documento, salida);

        documento.open();

        Font titulo = new Font(
                Font.HELVETICA,
                18,
                Font.BOLD
        );

        documento.add(
                new Paragraph(
                        "C&M ESTILOS - REPORTE DE VENTAS",
                        titulo
                )
        );

        String textoFechas =
                "Desde: "
                + (desde == null ? "Todas" : desde)
                + "    Hasta: "
                + (hasta == null ? "Todas" : hasta);

        documento.add(new Paragraph(textoFechas));
        documento.add(new Paragraph(" "));

        com.lowagie.text.pdf.PdfPTable tabla = new com.lowagie.text.pdf.PdfPTable(7);
        tabla.setWidthPercentage(100);

        tabla.addCell("ID");
        tabla.addCell("Fecha");
        tabla.addCell("Cliente");
        tabla.addCell("Tipo");
        tabla.addCell("Pago");
        tabla.addCell("Subtotal");
        tabla.addCell("Total");

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                );

        for (Venta venta : ventas) {

            tabla.addCell(
                    venta.getIdVenta() == null
                            ? "-"
                            : String.valueOf(
                                    venta.getIdVenta()
                            )
            );

            tabla.addCell(
                    venta.getFechaHora() == null
                            ? "-"
                            : venta.getFechaHora()
                                    .format(formato)
            );

            tabla.addCell(
                    venta.getCliente() == null
                            ? "-"
                            : venta.getCliente().getNombres()
            );

            tabla.addCell(
                    venta.getTipoVenta() == null
                            ? "-"
                            : venta.getTipoVenta().getValor()
            );

            tabla.addCell(
                    venta.getMetodoPago() == null
                            ? "-"
                            : venta.getMetodoPago().getNombre()
            );

            tabla.addCell(
                    "S/ "
                    + (venta.getSubtotal() == null
                            ? BigDecimal.ZERO
                            : venta.getSubtotal())
            );

            tabla.addCell(
                    "S/ "
                    + (venta.getMontoTotal() == null
                            ? BigDecimal.ZERO
                            : venta.getMontoTotal())
            );
        }

        documento.add(tabla);
        documento.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_ventas.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(salida.toByteArray());
    }

    private VentaResponse convertirVenta(Venta venta) {

        List<DetalleVentaResponse> detalles =
                detalleRepository
                        .findByVentaIdVenta(
                                venta.getIdVenta()
                        )
                        .stream()
                        .map(this::convertirDetalle)
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
                detalles
        );
    }

    private DetalleVentaResponse convertirDetalle(
            DetalleVenta detalle) {

        BigDecimal precioUnitario =
                detalle.getPrecioUnit() == null
                        ? BigDecimal.ZERO
                        : detalle.getPrecioUnit();

        BigDecimal totalDetalle =
                precioUnitario.multiply(
                        BigDecimal.valueOf(
                                detalle.getCantidad()
                        )
                );

        return new DetalleVentaResponse(
                detalle.getIdDetalle(),

                detalle.getVariante() == null
                        ? null
                        : detalle.getVariante()
                                .getIdVariante(),

                detalle.getVariante() == null
                        ? null
                        : detalle.getVariante()
                                .getProducto()
                                .getSku(),

                detalle.getVariante() == null
                        ? null
                        : detalle.getVariante()
                                .getProducto()
                                .getNombre(),

                detalle.getVariante() == null
                        ? null
                        : detalle.getVariante().getTalla(),

                detalle.getVariante() == null
                        ? null
                        : detalle.getVariante().getColor(),

                detalle.getCantidad(),
                precioUnitario,
                totalDetalle
        );
    }
}