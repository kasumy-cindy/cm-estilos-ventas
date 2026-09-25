package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.Venta;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);
    List<Venta> findAllByOrderByFechaHoraDesc();
}
