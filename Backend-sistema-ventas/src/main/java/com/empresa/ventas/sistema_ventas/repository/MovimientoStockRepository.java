package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.inventario.MovimientoStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    List<MovimientoStock> findByProductoIdOrderByCreatedAtDesc(Long productoId);
    List<MovimientoStock> findByReferenciaTipoAndReferenciaId(String tipo, Long id);
    Page<MovimientoStock> findByAlmacenId(Long almacenId, Pageable pageable);
}