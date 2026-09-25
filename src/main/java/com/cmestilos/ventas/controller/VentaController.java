package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.VentaRequest;
import com.cmestilos.ventas.dto.VentaResponse;
import com.cmestilos.ventas.service.VentaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    public ResponseEntity<VentaResponse> crear(
            @Valid @RequestBody VentaRequest request,
            Authentication authentication) {

        String username =
                authentication == null
                        ? null
                        : authentication.getName();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ventaService.crear(request, username));
    }

    @PostMapping("/online")
    @PreAuthorize("permitAll()")
    public ResponseEntity<VentaResponse> crearOnline(
            @Valid @RequestBody VentaRequest request) {

        if (request.getTipoVenta() == null
                || !"Online".equalsIgnoreCase(
                        request.getTipoVenta())) {

            throw new com.cmestilos.ventas.exception.BusinessException(
                    "El endpoint online solo acepta ventas de tipo Online"
            );
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ventaService.crear(request, null));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    public ResponseEntity<List<VentaResponse>> listar() {
        return ResponseEntity.ok(ventaService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    public ResponseEntity<VentaResponse> obtener(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                ventaService.obtener(id)
        );
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    public ResponseEntity<VentaResponse> actualizarEstado(
            @PathVariable Integer id,
            @RequestParam String estado) {

        return ResponseEntity.ok(
                ventaService.actualizarEstado(id, estado)
        );
    }
}