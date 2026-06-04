package com.empresa.ventas.sistema_ventas.repository;

import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    Optional<Venta> findByNumeroVenta(String numeroVenta);
    Page<Venta> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Venta> findByClienteId(Long clienteId, Pageable pageable);
    Page<Venta> findByEstadoVentaId(Long estadoId, Pageable pageable);
    Page<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    @Query("SELECT MAX(v.numeroVenta) FROM Venta v")
    String findMaxNumeroVenta();

    @Query("SELECT SUM(v.total) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE AND v.estadoVenta.codigo = 'PAGADO'")
    BigDecimal totalVentasHoy();
}