package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.ProveedorRequest;
import com.cmestilos.ventas.dto.ProveedorResponse;
import com.cmestilos.ventas.service.ProveedorService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ProveedorController {
    private final ProveedorService service;
    public ProveedorController(ProveedorService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<ProveedorResponse>> listar() { return ResponseEntity.ok(service.listar()); }
    @PostMapping public ResponseEntity<ProveedorResponse> crear(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
    @PutMapping("/{id}") public ResponseEntity<ProveedorResponse> actualizar(@PathVariable Integer id,
            @Valid @RequestBody ProveedorRequest request) { return ResponseEntity.ok(service.actualizar(id, request)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id); return ResponseEntity.noContent().build();
    }
}
