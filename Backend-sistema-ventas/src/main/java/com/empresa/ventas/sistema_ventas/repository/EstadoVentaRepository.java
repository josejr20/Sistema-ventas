package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.venta.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {
}