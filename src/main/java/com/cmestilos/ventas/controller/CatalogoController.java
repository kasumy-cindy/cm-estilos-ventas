package com.cmestilos.ventas.controller;

import com.cmestilos.ventas.dto.VarianteResponse;
import com.cmestilos.ventas.service.CatalogoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public ResponseEntity<List<VarianteResponse>> buscar(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "true") boolean soloDisponibles) {
        return ResponseEntity.ok(catalogoService.buscar(categoriaId, talla, color, nombre, soloDisponibles));
    }
}
