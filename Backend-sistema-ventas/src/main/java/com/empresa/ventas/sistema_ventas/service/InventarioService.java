package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.entity.inventario.*;
import com.empresa.ventas.sistema_ventas.entity.producto.Producto;
import com.empresa.ventas.sistema_ventas.exception.BusinessException;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;
    private final TipoMovimientoStockRepository tipoMovimientoStockRepository;
    private final MovimientoStockRepository movimientoStockRepository;

    public List<Inventario> consultarStock(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));
        return inventarioRepository.findByProductoId(productoId);
    }

    public Inventario consultarStock(Long productoId, Long almacenId) {
        return inventarioRepository.findByProductoIdAndAlmacenId(productoId, almacenId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "producto_id:almacen_id", productoId + ":" + almacenId));
    }

    public List<Inventario> alertasStockBajo() {
        return inventarioRepository.findStockBajo();
    }

    @Transactional
    public void ajustarStock(Long productoId, Long almacenId, BigDecimal cantidad, String motivo, Long usuarioId) {
        Almacen almacen = almacenRepository.findById(almacenId)
                .orElseThrow(() -> new ResourceNotFoundException("Almacen", "id", almacenId));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));

        Inventario inventario = inventarioRepository.findByProductoIdAndAlmacenId(productoId, almacenId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "producto_id:almacen_id", productoId + ":" + almacenId));

        BigDecimal stockAnterior = inventario.getStockActual();
        inventario.setStockActual(inventario.getStockActual().add(cantidad));

        TipoMovimientoStock tipo = tipoMovimientoStockRepository.findByCodigo("AJUSTE_POSITIVO")
                .orElseThrow(() -> new ResourceNotFoundException("TipoMovimientoStock", "codigo", "AJUSTE_POSITIVO"));

        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            tipo = tipoMovimientoStockRepository.findByCodigo("AJUSTE_NEGATIVO")
                    .orElseThrow(() -> new ResourceNotFoundException("TipoMovimientoStock", "codigo", "AJUSTE_NEGATIVO"));
        }

        MovimientoStock movimiento = MovimientoStock.builder()
                .producto(producto)
                .almacen(almacen)
                .tipoMovimiento(tipo)
                .cantidad(cantidad.abs())
                .stockAnterior(stockAnterior)
                .stockPosterior(inventario.getStockActual())
                .motivo(motivo)
                .build();
        movimientoStockRepository.save(movimiento);
    }

    @Transactional
    public void descontarStock(Long productoId, Long almacenId, BigDecimal cantidad, Long ventaId) {
        Inventario inventario = inventarioRepository.findByProductoIdAndAlmacenId(productoId, almacenId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario", "producto_id:almacen_id", productoId + ":" + almacenId));

        if (inventario.getStockActual().compareTo(cantidad) < 0) {
            throw new BusinessException("Stock insuficiente. Disponible: " + inventario.getStockActual() + ", solicitado: " + cantidad);
        }

        BigDecimal stockAnterior = inventario.getStockActual();
        inventario.setStockActual(inventario.getStockActual().subtract(cantidad));

        TipoMovimientoStock tipo = tipoMovimientoStockRepository.findByCodigo("SALIDA_VENTA")
                .orElseThrow(() -> new ResourceNotFoundException("TipoMovimientoStock", "codigo", "SALIDA_VENTA"));

        MovimientoStock movimiento = MovimientoStock.builder()
                .producto(productoRepository.findById(productoId).orElseThrow())
                .almacen(almacenRepository.findById(almacenId).orElseThrow())
                .tipoMovimiento(tipo)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockPosterior(inventario.getStockActual())
                .referenciaTipo("VENTA")
                .referenciaId(ventaId)
                .build();
        movimientoStockRepository.save(movimiento);
    }
}