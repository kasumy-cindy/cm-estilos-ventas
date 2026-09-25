package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.Proveedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    boolean existsByRuc(String ruc);
    Optional<Proveedor> findByRuc(String ruc);
}
