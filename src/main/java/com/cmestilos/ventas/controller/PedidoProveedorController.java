package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.PedidoProveedorRequest;
import com.cmestilos.ventas.dto.PedidoProveedorResponse;
import com.cmestilos.ventas.service.PedidoProveedorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos-proveedor")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class PedidoProveedorController {
    private final PedidoProveedorService service;
    public PedidoProveedorController(PedidoProveedorService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<PedidoProveedorResponse>> listar() { return ResponseEntity.ok(service.listar()); }
    @PostMapping public ResponseEntity<PedidoProveedorResponse> crear(@Valid @RequestBody PedidoProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoProveedorResponse> cambiarEstado(@PathVariable Integer id,
            @RequestParam String estado) { return ResponseEntity.ok(service.cambiarEstado(id, estado)); }
}
