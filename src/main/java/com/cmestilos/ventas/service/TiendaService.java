package com.cmestilos.ventas.service;

import com.cmestilos.ventas.dto.ClienteRequest;
import com.cmestilos.ventas.dto.TiendaCheckoutRequest;
import com.cmestilos.ventas.dto.VentaRequest;
import com.cmestilos.ventas.dto.VentaResponse;
import com.cmestilos.ventas.entity.Cliente;
import com.cmestilos.ventas.exception.BusinessException;
import com.cmestilos.ventas.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TiendaService {

    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;
    private final VentaService ventaService;

    public TiendaService(
            ClienteRepository clienteRepository,
            ClienteService clienteService,
            VentaService ventaService) {

        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
        this.ventaService = ventaService;
    }

    @Transactional
    public VentaResponse crearPedido(TiendaCheckoutRequest request) {

        String documento = request.getNumDocumento().trim();

        String modalidad = request.getModalidadEntrega();

        if (modalidad == null || modalidad.isBlank()) {
            modalidad = "RECOJO_TIENDA";
        }

        modalidad = modalidad.trim().toUpperCase();

        String direccion = request.getDireccionEntrega();

        if ("DELIVERY".equals(modalidad)) {
            if (direccion == null || direccion.isBlank()) {
                throw new BusinessException(
                        "La dirección es obligatoria para la entrega a domicilio"
                );
            }

            direccion = direccion.trim();
        } else {
            modalidad = "RECOJO_TIENDA";
            direccion = null;
        }

        Cliente cliente = clienteRepository
                .findByNumDocumento(documento)
                .orElse(null);

        Integer clienteId;

        if (cliente == null) {

            ClienteRequest clienteRequest = new ClienteRequest(
                    request.getTipoDocumento(),
                    documento,
                    request.getNombres(),
                    request.getCorreo(),
                    request.getTelefono()
            );

            clienteId = clienteService
                    .crear(clienteRequest)
                    .getIdCliente();

        } else {
            clienteId = cliente.getIdCliente();
        }

        String telefono = request.getTelefono();

        if (telefono != null && !telefono.isBlank()) {
            telefono = telefono.trim();
        }

        VentaRequest ventaRequest = new VentaRequest(
                clienteId,
                "Online",
                request.getMetodoPagoId(),
                modalidad,
                direccion,
                telefono,
                request.getItems()
        );

        return ventaService.crear(ventaRequest, null);
    }

    @Transactional(readOnly = true)
    public VentaResponse consultarPedido(
            Integer id,
            String correo) {

        return ventaService.consultarPorCliente(id, correo);
    }

    @Transactional
    public VentaResponse cancelarPedido(
            Integer id,
            String correo) {

        return ventaService.cancelarPorCliente(id, correo);
    }
}