package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.MetodoPagoRequest;
import com.cmestilos.ventas.dto.MetodoPagoResponse;
import com.cmestilos.ventas.service.MetodoPagoService;
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
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoService service;

    public MetodoPagoController(MetodoPagoService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAJERO')")
    public ResponseEntity<List<MetodoPagoResponse>> listar(
            @RequestParam(defaultValue = "true") boolean soloActivos) {
        return ResponseEntity.ok(service.listar(soloActivos));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MetodoPagoResponse> crear(@Valid @RequestBody MetodoPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MetodoPagoResponse> actualizar(@PathVariable Integer id,
                                                          @Valid @RequestBody MetodoPagoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
