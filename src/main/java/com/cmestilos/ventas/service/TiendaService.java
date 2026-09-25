package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.ClienteRequest;
import com.cmestilos.ventas.dto.TiendaCheckoutRequest;
import com.cmestilos.ventas.dto.VentaRequest;
import com.cmestilos.ventas.dto.VentaResponse;
import com.cmestilos.ventas.entity.Cliente;
import com.cmestilos.ventas.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TiendaService {
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;
    private final VentaService ventaService;

    public TiendaService(ClienteRepository clienteRepository, ClienteService clienteService,
                         VentaService ventaService) {
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
        this.ventaService = ventaService;
    }

    @Transactional
    public VentaResponse crearPedido(TiendaCheckoutRequest request) {
        String documento = request.getNumDocumento().trim();
        Cliente cliente = clienteRepository.findByNumDocumento(documento).orElse(null);
        Integer clienteId;

        if (cliente == null) {
            ClienteRequest clienteRequest = new ClienteRequest(
                    request.getTipoDocumento(), documento, request.getNombres(),
                    request.getCorreo(), request.getTelefono());
            clienteId = clienteService.crear(clienteRequest).getIdCliente();
        } else {
            clienteId = cliente.getIdCliente();
        }

        VentaRequest ventaRequest = new VentaRequest(
                clienteId, "Online", request.getMetodoPagoId(), request.getItems());
        return ventaService.crear(ventaRequest, null);
    }
}
