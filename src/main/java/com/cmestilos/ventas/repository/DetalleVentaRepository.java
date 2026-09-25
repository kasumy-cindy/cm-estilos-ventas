package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.DetalleVenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    List<DetalleVenta> findByVentaIdVenta(Integer idVenta);
}
