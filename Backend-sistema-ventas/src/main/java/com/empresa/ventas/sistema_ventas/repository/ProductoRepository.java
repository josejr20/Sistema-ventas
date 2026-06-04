package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.producto.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigoBarras(String barras);
    Optional<Producto> findByCodigo(String codigo);
    Page<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId, Pageable pageable);
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);
    boolean existsByCodigoBarras(String barras);
    boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.nombre ASC")
    List<Producto> findAllActivos();
}