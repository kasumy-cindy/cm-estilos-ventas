package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.ResenaRequest;
import com.cmestilos.ventas.dto.ResenaResponse;
import com.cmestilos.ventas.dto.ResenaResumenResponse;
import com.cmestilos.ventas.entity.ResenaProducto;
import com.cmestilos.ventas.repository.ResenaProductoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResenaService {
    private final ResenaProductoRepository repository;
    private final ProductoService productoService;

    public ResenaService(ResenaProductoRepository repository, ProductoService productoService) {
        this.repository = repository;
        this.productoService = productoService;
    }

    @Transactional(readOnly = true)
    public ResenaResumenResponse listar(Integer productoId) {
        List<ResenaProducto> resenas = repository
                .findByProductoIdProductoAndAprobadaTrueOrderByFechaHoraDesc(productoId);
        List<ResenaResponse> respuestas = resenas.stream().map(this::toResponse).toList();
        BigDecimal promedio = resenas.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(resenas.stream().mapToInt(ResenaProducto::getPuntuacion).average().orElse(0))
                        .setScale(1, RoundingMode.HALF_UP);
        return new ResenaResumenResponse(productoId, promedio, respuestas.size(), respuestas);
    }

    @Transactional
    public ResenaResponse crear(Integer productoId, ResenaRequest request) {
        ResenaProducto resena = new ResenaProducto();
        resena.setProducto(productoService.buscarEntidad(productoId));
        resena.setNombreCliente(request.getNombreCliente().trim());
        resena.setCorreo(request.getCorreo() == null ? null : request.getCorreo().trim());
        resena.setPuntuacion(request.getPuntuacion());
        resena.setComentario(request.getComentario().trim());
        resena.setFechaHora(LocalDateTime.now());
        resena.setAprobada(true);
        return toResponse(repository.save(resena));
    }

    private ResenaResponse toResponse(ResenaProducto resena) {
        return new ResenaResponse(resena.getIdResena(), resena.getNombreCliente(),
                resena.getPuntuacion(), resena.getComentario(), resena.getFechaHora());
    }
}
