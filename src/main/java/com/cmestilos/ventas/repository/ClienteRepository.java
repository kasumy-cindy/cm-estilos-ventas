package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByNumDocumento(String numDocumento);
    Optional<Cliente> findByCorreoIgnoreCase(String correo);
    boolean existsByNumDocumento(String numDocumento);
}
