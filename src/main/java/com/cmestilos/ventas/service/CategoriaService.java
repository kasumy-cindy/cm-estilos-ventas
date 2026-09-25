package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.CategoriaRequest;
import com.cmestilos.ventas.dto.CategoriaResponse;
import com.cmestilos.ventas.entity.Categoria;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Integer id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.findByNombreIgnoreCase(request.getNombre()).isPresent()) {
            throw new BusinessException("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre().trim());
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Integer id, CategoriaRequest request) {
        Categoria categoria = buscarEntidad(id);
        categoriaRepository.findByNombreIgnoreCase(request.getNombre()).ifPresent(otra -> {
            if (!otra.getIdCategoria().equals(id)) {
                throw new BusinessException("Ya existe una categoría con ese nombre");
            }
        });
        categoria.setNombre(request.getNombre().trim());
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Integer id) {
        categoriaRepository.delete(buscarEntidad(id));
    }

    public Categoria buscarEntidad(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(categoria.getIdCategoria(), categoria.getNombre());
    }
}
