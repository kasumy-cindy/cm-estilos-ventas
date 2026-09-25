package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.InventarioUpdateRequest;
import com.cmestilos.ventas.dto.VarianteRequest;
import com.cmestilos.ventas.dto.VarianteResponse;
import com.cmestilos.ventas.service.InventarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    public ResponseEntity<List<VarianteResponse>> listar(
            @RequestParam(defaultValue = "false") boolean soloCriticos) {
        return ResponseEntity.ok(inventarioService.listar(soloCriticos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    public ResponseEntity<VarianteResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(inventarioService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<VarianteResponse> crear(@Valid @RequestBody VarianteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<VarianteResponse> actualizarStock(
            @PathVariable Integer id, @Valid @RequestBody InventarioUpdateRequest request) {
        return ResponseEntity.ok(inventarioService.actualizarStock(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
