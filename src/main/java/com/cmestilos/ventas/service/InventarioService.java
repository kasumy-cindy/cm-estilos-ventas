package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.InventarioUpdateRequest;
import com.cmestilos.ventas.dto.VarianteRequest;
import com.cmestilos.ventas.dto.VarianteResponse;
import com.cmestilos.ventas.entity.Producto;
import com.cmestilos.ventas.entity.VarianteProducto;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.VarianteProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioService {

    private final VarianteProductoRepository varianteRepository;
    private final ProductoService productoService;

    public InventarioService(VarianteProductoRepository varianteRepository,
                             ProductoService productoService) {
        this.varianteRepository = varianteRepository;
        this.productoService = productoService;
    }

    @Transactional(readOnly = true)
    public List<VarianteResponse> listar(boolean soloCriticos) {
        List<VarianteProducto> variantes = soloCriticos
                ? varianteRepository.findVariantesEnStockCritico()
                : varianteRepository.findAll();
        return variantes.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VarianteResponse obtener(Integer id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional
    public VarianteResponse crear(VarianteRequest request) {
        Producto producto = productoService.buscarEntidad(request.getProductoId());
        if (varianteRepository.findByProductoIdProductoAndTallaIgnoreCaseAndColorIgnoreCase(
                request.getProductoId(), request.getTalla(), request.getColor()).isPresent()) {
            throw new BusinessException("Ya existe esa talla y color para el producto");
        }
        VarianteProducto variante = new VarianteProducto();
        variante.setProducto(producto);
        variante.setTalla(request.getTalla().trim());
        variante.setColor(request.getColor().trim());
        variante.setStockActual(request.getStockActual());
        variante.setStockCritico(request.getStockCritico());
        return toResponse(varianteRepository.save(variante));
    }

    @Transactional
    public VarianteResponse actualizarStock(Integer id, InventarioUpdateRequest request) {
        VarianteProducto variante = buscarEntidad(id);
        variante.setStockActual(request.getStockActual());
        variante.setStockCritico(request.getStockCritico());
        return toResponse(varianteRepository.save(variante));
    }

    @Transactional
    public void eliminar(Integer id) {
        varianteRepository.delete(buscarEntidad(id));
    }

    public VarianteProducto buscarEntidad(Integer id) {
        return varianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada: " + id));
    }

    private VarianteResponse toResponse(VarianteProducto variante) {
        Producto producto = variante.getProducto();
        return new VarianteResponse(variante.getIdVariante(), producto.getIdProducto(), producto.getSku(),
                producto.getNombre(), variante.getTalla(), variante.getColor(),
                producto.getPrecioVenta(),
                producto.getImagenUrl(),
                variante.getStockActual(), variante.getStockCritico(),
                variante.getStockActual() <= variante.getStockCritico());
    }
}
