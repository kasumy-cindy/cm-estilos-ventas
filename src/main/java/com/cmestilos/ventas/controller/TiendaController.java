package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.MetodoPagoResponse;
import com.cmestilos.ventas.dto.TiendaCheckoutRequest;
import com.cmestilos.ventas.dto.VentaResponse;
import com.cmestilos.ventas.service.MetodoPagoService;
import com.cmestilos.ventas.service.TiendaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tienda")
public class TiendaController {
    private final TiendaService tiendaService;
    private final MetodoPagoService metodoPagoService;

    public TiendaController(TiendaService tiendaService, MetodoPagoService metodoPagoService) {
        this.tiendaService = tiendaService;
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping("/metodos-pago")
    public ResponseEntity<List<MetodoPagoResponse>> metodosPago() {
        return ResponseEntity.ok(metodoPagoService.listar(true));
    }

    @PostMapping("/checkout")
    public ResponseEntity<VentaResponse> checkout(@Valid @RequestBody TiendaCheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tiendaService.crearPedido(request));
    }
}
