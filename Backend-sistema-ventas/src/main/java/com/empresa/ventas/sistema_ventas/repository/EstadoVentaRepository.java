package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.venta.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {
    Optional<EstadoVenta> findByCodigo(String codigo);
}