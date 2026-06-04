package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.pago.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoPagoRepository extends JpaRepository<EstadoPago, Long> {
    Optional<EstadoPago> findByCodigo(String codigo);
}