package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.comprobante.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Long> {
    Optional<TipoComprobante> findByCodigo(String codigo);
    Optional<TipoComprobante> findTopByTipoComprobanteIdOrderByCorrelativoDesc(Long tipoId);
}