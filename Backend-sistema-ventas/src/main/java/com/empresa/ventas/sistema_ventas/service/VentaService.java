package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.dto.request.DetalleVentaRequest;
import com.empresa.ventas.sistema_ventas.dto.request.PagoRequest;
import com.empresa.ventas.sistema_ventas.dto.request.VentaRequest;
import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.entity.cliente.Cliente;
import com.empresa.ventas.sistema_ventas.entity.inventario.Almacen;
import com.empresa.ventas.sistema_ventas.entity.pago.EstadoPago;
import com.empresa.ventas.sistema_ventas.entity.pago.EstadoPago;
import com.empresa.ventas.sistema_ventas.entity.producto.Producto;
import com.empresa.ventas.sistema_ventas.entity.venta.DetalleVenta;
import com.empresa.ventas.sistema_ventas.entity.venta.EstadoVenta;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import com.empresa.ventas.sistema_ventas.exception.BusinessException;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.*;
import com.empresa.ventas.sistema_ventas.util.NumeroVentaGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final PagoRepository pagoRepository;
    private final InventarioService inventarioService;
    private final NumeroVentaGenerator numeroVentaGenerator;

    @Transactional
    public Venta crearVenta(VentaRequest request, Long usuarioId) {
        Almacen almacen = almacenRepository.findById(request.getAlmacenId())
                .orElseThrow(() -> new ResourceNotFoundException("Almacen", "id", request.getAlmacenId()));

        EstadoVenta estadoPendiente = estadoVentaRepository.findByCodigo("PENDIENTE")
                .orElseThrow(() -> new ResourceNotFoundException("EstadoVenta", "codigo", "PENDIENTE"));

        Venta venta = Venta.builder()
                .uuid(UUID.randomUUID())
                .numeroVenta(numeroVentaGenerator.generarNumeroVenta())
                .cliente(clienteRepository.findById(request.getClienteId()).orElse(null))
                .usuario(Usuario.builder().id(usuarioId).build())
                .almacen(almacen)
                .estadoVenta(estadoPendiente)
                .moneda(request.getMoneda() != null ? request.getMoneda() : "PEN")
                .observaciones(request.getObservaciones())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        BigDecimal igvTotal = BigDecimal.ZERO;

        for (DetalleVentaRequest detalleReq : request.getDetalles()) {
            Producto producto = productoRepository.findById(detalleReq.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", detalleReq.getProductoId()));

            if (!producto.isActivo()) {
                throw new BusinessException("Producto no activo: " + producto.getNombre());
            }

            BigDecimal cantidad = detalleReq.getCantidad();
            BigDecimal precioUnitario = producto.getPrecioVenta();
            BigDecimal descuentoPorc = detalleReq.getDescuentoPorcentaje() != null ? detalleReq.getDescuentoPorcentaje() : BigDecimal.ZERO;

            BigDecimal descuentoMonto = precioUnitario.multiply(descuentoPorc).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal precioFinal = precioUnitario.subtract(descuentoMonto);
            BigDecimal subtotalLinea = precioFinal.multiply(cantidad);

            BigDecimal igvLinea = BigDecimal.ZERO;
            if (producto.isAfectoIgv()) {
                igvLinea = subtotalLinea.multiply(BigDecimal.valueOf(0.18)).setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal totalLinea = subtotalLinea.add(igvLinea);

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(precioUnitario)
                    .descuentoPorcentaje(descuentoPorc)
                    .descuentoMonto(descuentoMonto)
                    .precioFinal(precioFinal)
                    .subtotal(subtotalLinea)
                    .igv(igvLinea)
                    .total(totalLinea)
                    .productoNombre(producto.getNombre())
                    .productoCodigo(producto.getCodigo())
                    .build();

            venta.getDetalles().add(detalle);
            subtotal = subtotal.add(subtotalLinea);
            descuentoTotal = descuentoTotal.add(descuentoMonto.multiply(cantidad));
            igvTotal = igvTotal.add(igvLinea);
        }

        venta.setSubtotal(subtotal);
        venta.setDescuentoTotal(descuentoTotal);
        venta.setIgv(igvTotal);
        venta.setTotal(subtotal.add(igvTotal));

        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta confirmarPago(Long ventaId, PagoRequest request) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", ventaId));

        EstadoVenta estadoPendiente = estadoVentaRepository.findByCodigo("PENDIENTE")
                .orElseThrow(() -> new ResourceNotFoundException("EstadoVenta", "codigo", "PENDIENTE"));

        EstadoPago estadoAprobado = estadoPagoRepository.findByCodigo("APROBADO")
                .orElseThrow(() -> new ResourceNotFoundException("EstadoPago", "codigo", "APROBADO"));

        if (!venta.getEstadoVenta().getCodigo().equals("PENDIENTE")) {
            throw new BusinessException("La venta no está en estado PENDIENTE");
        }

        EstadoVenta estadoPagado = estadoVentaRepository.findByCodigo("PAGADO")
                .orElseThrow(() -> new ResourceNotFoundException("EstadoVenta", "codigo", "PAGADO"));

        venta.setEstadoVenta(estadoPagado);
        ventaRepository.save(venta);

        return venta;
    }

    @Transactional
    public Venta anularVenta(Long ventaId, String motivo, Long usuarioId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", ventaId));

        if (venta.getEstadoVenta().getCodigo().equals("ANULADO")) {
            throw new BusinessException("La venta ya está anulada");
        }

        EstadoVenta estadoAnulado = estadoVentaRepository.findByCodigo("ANULADO")
                .orElseThrow(() -> new ResourceNotFoundException("EstadoVenta", "codigo", "ANULADO"));

        venta.setEstadoVenta(estadoAnulado);
        venta.setFechaAnulacion(LocalDateTime.now());
        venta.setAnuladoPor(usuarioId);
        venta.setMotivoAnulacion(motivo);

        return ventaRepository.save(venta);
    }

    public Page<Venta> listar(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));
    }
}