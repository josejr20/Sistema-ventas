package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.inventario.TipoMovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoMovimientoStockRepository extends JpaRepository<TipoMovimientoStock, Long> {
    Optional<TipoMovimientoStock> findByCodigo(String codigo);
}