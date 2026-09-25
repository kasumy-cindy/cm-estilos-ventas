package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.ResenaRequest;
import com.cmestilos.ventas.dto.ResenaResponse;
import com.cmestilos.ventas.dto.ResenaResumenResponse;
import com.cmestilos.ventas.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tienda/productos/{productoId}/resenas")
public class ResenaController {
    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping
    public ResponseEntity<ResenaResumenResponse> listar(@PathVariable Integer productoId) {
        return ResponseEntity.ok(resenaService.listar(productoId));
    }

    @PostMapping
    public ResponseEntity<ResenaResponse> crear(@PathVariable Integer productoId,
                                                @Valid @RequestBody ResenaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(productoId, request));
    }
}
