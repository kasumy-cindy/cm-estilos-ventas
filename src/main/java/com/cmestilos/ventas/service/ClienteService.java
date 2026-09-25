package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.ClienteRequest;
import com.cmestilos.ventas.dto.ClienteResponse;
import com.cmestilos.ventas.entity.Cliente;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.exception.ResourceNotFoundException;
import com.cmestilos.ventas.repository.ClienteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Integer id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByNumDocumento(request.getNumDocumento())) {
            throw new BusinessException("Ya existe un cliente con ese documento");
        }
        Cliente cliente = new Cliente();
        copiar(request, cliente);
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse actualizar(Integer id, ClienteRequest request) {
        Cliente cliente = buscarEntidad(id);
        clienteRepository.findByNumDocumento(request.getNumDocumento()).ifPresent(otro -> {
            if (!otro.getIdCliente().equals(id)) {
                throw new BusinessException("Ya existe otro cliente con ese documento");
            }
        });
        copiar(request, cliente);
        return toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Integer id) {
        clienteRepository.delete(buscarEntidad(id));
    }

    public Cliente buscarEntidad(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
    }

    private void copiar(ClienteRequest request, Cliente cliente) {
        cliente.setTipoDocumento(request.getTipoDocumento().trim());
        cliente.setNumDocumento(request.getNumDocumento().trim());
        cliente.setNombres(request.getNombres().trim());
        cliente.setCorreo(request.getCorreo() == null ? null : request.getCorreo().trim());
        cliente.setTelefono(request.getTelefono() == null ? null : request.getTelefono().trim());
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getIdCliente(), cliente.getTipoDocumento(),
                cliente.getNumDocumento(), cliente.getNombres(), cliente.getCorreo(), cliente.getTelefono());
    }
}
