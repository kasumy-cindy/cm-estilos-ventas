package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.DetallePedidoProveedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoProveedorRepository extends JpaRepository<DetallePedidoProveedor, Integer> {
    List<DetallePedidoProveedor> findByPedidoIdPedido(Integer idPedido);
}
