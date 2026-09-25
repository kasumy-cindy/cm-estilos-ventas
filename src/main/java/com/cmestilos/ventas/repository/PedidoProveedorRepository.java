package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.PedidoProveedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoProveedorRepository extends JpaRepository<PedidoProveedor, Integer> {
    List<PedidoProveedor> findAllByOrderByFechaSolicitudDesc();
}
