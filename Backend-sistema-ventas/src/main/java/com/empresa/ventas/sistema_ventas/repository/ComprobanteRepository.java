package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.comprobante.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    Optional<Comprobante> findByVentaId(Long ventaId);
    Optional<Comprobante> findByNumeroCompleto(String numero);
    Optional<Comprobante> findTopByTipoComprobanteIdOrderByCorrelativoDesc(Long tipoId);
}