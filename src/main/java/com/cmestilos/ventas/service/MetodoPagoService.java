package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.MetodoPagoRequest;
import com.cmestilos.ventas.dto.MetodoPagoResponse;
import com.cmestilos.ventas.entity.MetodoPago;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.MetodoPagoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MetodoPagoService {

    private final MetodoPagoRepository repository;

    public MetodoPagoService(MetodoPagoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listar(boolean soloActivos) {
        return repository.findAll().stream()
                .filter(m -> !soloActivos || Boolean.TRUE.equals(m.getActivo()))
                .map(this::toResponse).toList();
    }

    @Transactional
    public MetodoPagoResponse crear(MetodoPagoRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new BusinessException("Ya existe ese método de pago");
        }
        MetodoPago metodo = new MetodoPago();
        metodo.setNombre(request.getNombre().trim());
        metodo.setActivo(request.getActivo() == null || request.getActivo());
        return toResponse(repository.save(metodo));
    }

    @Transactional
    public MetodoPagoResponse actualizar(Integer id, MetodoPagoRequest request) {
        MetodoPago metodo = buscarEntidad(id);
        repository.findByNombreIgnoreCase(request.getNombre()).ifPresent(otro -> {
            if (!otro.getIdMetodoPago().equals(id)) {
                throw new BusinessException("Ya existe otro método de pago con ese nombre");
            }
        });
        metodo.setNombre(request.getNombre().trim());
        if (request.getActivo() != null) metodo.setActivo(request.getActivo());
        return toResponse(repository.save(metodo));
    }

    @Transactional
    public void eliminar(Integer id) {
        repository.delete(buscarEntidad(id));
    }

    public MetodoPago buscarEntidad(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado: " + id));
    }

    private MetodoPagoResponse toResponse(MetodoPago metodo) {
        return new MetodoPagoResponse(metodo.getIdMetodoPago(), metodo.getNombre(), metodo.getActivo());
    }
}
