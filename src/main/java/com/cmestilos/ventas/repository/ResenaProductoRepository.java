package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.ResenaProducto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaProductoRepository extends JpaRepository<ResenaProducto, Integer> {
    List<ResenaProducto> findByProductoIdProductoAndAprobadaTrueOrderByFechaHoraDesc(Integer idProducto);
}
