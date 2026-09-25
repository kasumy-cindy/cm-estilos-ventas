package com.cmestilos.ventas.repository;

import com.cmestilos.ventas.entity.VarianteProducto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VarianteProductoRepository
        extends JpaRepository<VarianteProducto, Integer> {

    List<VarianteProducto> findByStockActualLessThanEqual(
            Integer stockCritico
    );

    List<VarianteProducto> findByProductoIdProducto(
            Integer idProducto
    );

    Optional<VarianteProducto>
    findByProductoIdProductoAndTallaIgnoreCaseAndColorIgnoreCase(
            Integer idProducto,
            String talla,
            String color
    );

    @Query("""
        SELECT v
        FROM VarianteProducto v
        JOIN FETCH v.producto p
        JOIN FETCH p.categoria
        WHERE v.stockActual <= v.stockCritico
        ORDER BY v.stockActual ASC
        """)
    List<VarianteProducto> findVariantesEnStockCritico();

    @Query("""
        SELECT v
        FROM VarianteProducto v
        JOIN FETCH v.producto p
        JOIN FETCH p.categoria
        WHERE p.activo = true
        AND (:categoriaId IS NULL
             OR p.categoria.idCategoria = :categoriaId)
        AND (:talla = ''
             OR LOWER(v.talla) = LOWER(:talla))
        AND (:color = ''
             OR LOWER(v.color) = LOWER(:color))
        AND (:nombre = ''
             OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
        AND (:soloDisponibles = false
             OR v.stockActual > 0)
        """)
    List<VarianteProducto> buscarCatalogo(
            @Param("categoriaId") Integer categoriaId,
            @Param("talla") String talla,
            @Param("color") String color,
            @Param("nombre") String nombre,
            @Param("soloDisponibles") boolean soloDisponibles
    );
}