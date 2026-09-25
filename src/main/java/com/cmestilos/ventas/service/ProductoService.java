package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.ProductoRequest;
import com.cmestilos.ventas.dto.ProductoResponse;
import com.cmestilos.ventas.dto.VarianteResponse;
import com.cmestilos.ventas.entity.Producto;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String nombre, Integer categoriaId) {
        List<Producto> productos;
        if (nombre != null && !nombre.isBlank()) {
            productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (categoriaId != null) {
            productos = productoRepository.findByCategoriaIdCategoria(categoriaId);
        } else {
            productos = productoRepository.findAll();
        }
        return productos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Integer id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsBySku(request.getSku())) {
            throw new BusinessException("Ya existe un producto con el SKU indicado");
        }
        Producto producto = new Producto();
        producto.setCategoria(categoriaService.buscarEntidad(request.getCategoriaId()));
        producto.setSku(request.getSku().trim());
        producto.setNombre(request.getNombre().trim());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setImagenUrl(limpiarImagen(request.getImagenUrl()));
        producto.setActivo(request.getActivo() == null || request.getActivo());
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Integer id, ProductoRequest request) {
        Producto producto = buscarEntidad(id);
        productoRepository.findBySku(request.getSku()).ifPresent(otro -> {
            if (!otro.getIdProducto().equals(id)) {
                throw new BusinessException("Ya existe otro producto con el SKU indicado");
            }
        });
        producto.setCategoria(categoriaService.buscarEntidad(request.getCategoriaId()));
        producto.setSku(request.getSku().trim());
        producto.setNombre(request.getNombre().trim());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setImagenUrl(limpiarImagen(request.getImagenUrl()));
        if (request.getActivo() != null) {
            producto.setActivo(request.getActivo());
        }
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse cambiarEstado(Integer id, boolean activo) {
        Producto producto = buscarEntidad(id);
        producto.setActivo(activo);
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Integer id) {
        productoRepository.delete(buscarEntidad(id));
    }

    public Producto buscarEntidad(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }

    private ProductoResponse toResponse(Producto producto) {
        List<VarianteResponse> variantes = producto.getVariantes().stream()
                .map(variante -> new VarianteResponse(
                        variante.getIdVariante(), producto.getIdProducto(), producto.getSku(),
                        producto.getNombre(), variante.getTalla(), variante.getColor(),
                        producto.getPrecioVenta(),
                        producto.getImagenUrl(),
                        variante.getStockActual(), variante.getStockCritico(),
                        variante.getStockActual() <= variante.getStockCritico()))
                .toList();
        return new ProductoResponse(producto.getIdProducto(), producto.getCategoria().getIdCategoria(),
                producto.getCategoria().getNombre(), producto.getSku(), producto.getNombre(),
                producto.getPrecioVenta(), producto.getImagenUrl(), producto.getActivo(), variantes);
    }

    private String limpiarImagen(String imagenUrl) {
        return imagenUrl == null || imagenUrl.isBlank() ? null : imagenUrl.trim();
    }
}
