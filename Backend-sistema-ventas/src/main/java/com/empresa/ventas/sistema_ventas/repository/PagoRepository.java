package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.pago.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByVentaId(Long ventaId);
    Optional<Pago> findByReferenciaPasarela(String referencia);
    List<Pago> findByEstadoPagoId(Long estadoId);
}