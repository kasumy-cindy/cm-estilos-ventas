package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaIdCategoria(Integer idCategoria);
}
