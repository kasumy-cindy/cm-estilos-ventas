package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.ProveedorRequest;
import com.cmestilos.ventas.dto.ProveedorResponse;
import com.cmestilos.ventas.entity.Proveedor;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.ProveedorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProveedorService {
    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        if (repository.existsByRuc(request.getRuc())) {
            throw new BusinessException("Ya existe un proveedor con ese RUC");
        }
        Proveedor p = new Proveedor();
        copiar(request, p);
        return toResponse(repository.save(p));
    }

    @Transactional
    public ProveedorResponse actualizar(Integer id, ProveedorRequest request) {
        Proveedor p = buscarEntidad(id);
        repository.findByRuc(request.getRuc()).ifPresent(otro -> {
            if (!otro.getIdProveedor().equals(id)) throw new BusinessException("El RUC ya está registrado");
        });
        copiar(request, p);
        return toResponse(repository.save(p));
    }

    @Transactional
    public void eliminar(Integer id) { repository.delete(buscarEntidad(id)); }

    public Proveedor buscarEntidad(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id));
    }

    private void copiar(ProveedorRequest request, Proveedor p) {
        p.setRuc(request.getRuc().trim()); p.setRazonSocial(request.getRazonSocial().trim());
        p.setContacto(limpiar(request.getContacto())); p.setTelefono(limpiar(request.getTelefono()));
        p.setCorreo(limpiar(request.getCorreo())); p.setDireccion(limpiar(request.getDireccion()));
        if (request.getActivo() != null) p.setActivo(request.getActivo());
    }

    private String limpiar(String s) { return s == null || s.isBlank() ? null : s.trim(); }

    private ProveedorResponse toResponse(Proveedor p) {
        return new ProveedorResponse(p.getIdProveedor(), p.getRuc(), p.getRazonSocial(), p.getContacto(),
                p.getTelefono(), p.getCorreo(), p.getDireccion(), p.getActivo());
    }
}
