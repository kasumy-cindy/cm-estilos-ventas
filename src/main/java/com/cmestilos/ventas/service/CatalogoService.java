package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.VarianteResponse;
import com.cmestilos.ventas.entity.Producto;
import com.cmestilos.ventas.entity.VarianteProducto;
import com.cmestilos.ventas.repository.VarianteProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogoService {

    private final VarianteProductoRepository varianteRepository;

    public CatalogoService(
            VarianteProductoRepository varianteRepository
    ) {
        this.varianteRepository = varianteRepository;
    }

    @Transactional(readOnly = true)
    public List<VarianteResponse> buscar(
            Integer categoriaId,
            String talla,
            String color,
            String nombre,
            boolean soloDisponibles
    ) {
        String tallaFiltro = convertirFiltro(talla);
        String colorFiltro = convertirFiltro(color);
        String nombreFiltro = convertirFiltro(nombre);

        return varianteRepository.buscarCatalogo(
                categoriaId,
                tallaFiltro,
                colorFiltro,
                nombreFiltro,
                soloDisponibles
        )
        .stream()
        .map(this::toResponse)
        .toList();
    }

    private String convertirFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }

        return valor.trim();
    }

    private VarianteResponse toResponse(
            VarianteProducto variante
    ) {
        Producto producto = variante.getProducto();

        return new VarianteResponse(
                variante.getIdVariante(),
                producto.getIdProducto(),
                producto.getSku(),
                producto.getNombre(),
                variante.getTalla(),
                variante.getColor(),
                producto.getPrecioVenta(),
                producto.getImagenUrl(),
                variante.getStockActual(),
                variante.getStockCritico(),
                variante.getStockActual()
                        <= variante.getStockCritico()
        );
    }
}