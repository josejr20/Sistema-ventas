package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.dto.request.PagoRequest;
import com.empresa.ventas.sistema_ventas.entity.pago.EstadoPago;
import com.empresa.ventas.sistema_ventas.entity.pago.MetodoPago;
import com.empresa.ventas.sistema_ventas.entity.pago.Pago;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.EstadoPagoRepository;
import com.empresa.ventas.sistema_ventas.repository.MetodoPagoRepository;
import com.empresa.ventas.sistema_ventas.repository.PagoRepository;
import com.empresa.ventas.sistema_ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoPagoRepository estadoPagoRepository;

    @Transactional
    public Pago procesarPago(PagoRequest request) {
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", request.getVentaId()));

        MetodoPago metodoPago = metodoPagoRepository.findById(request.getMetodoPagoId())
                .orElseThrow(() -> new ResourceNotFoundException("MetodoPago", "id", request.getMetodoPagoId()));

        EstadoPago estadoAprobado = estadoPagoRepository.findByCodigo("APROBADO")
                .orElseThrow(() -> new ResourceNotFoundException("EstadoPago", "codigo", "APROBADO"));

        Pago pago = Pago.builder()
                .uuid(UUID.randomUUID())
                .venta(venta)
                .metodoPago(metodoPago)
                .estadoPago(estadoAprobado)
                .monto(request.getMonto())
                .referenciaPasarela(request.getReferenciaExterna())
                .numeroOperacion(request.getNumeroOperacion())
                .telefonoOrigen(request.getTelefonoOrigen())
                .build();

        return pagoRepository.save(pago);
    }

    public void procesarWebhook(String payload, String firma) {
        log.info("Webhook recibido: payload={}, firma={}", payload, firma);
    }

    public Pago buscarPorVenta(Long ventaId) {
        return pagoRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "ventaId", ventaId));
    }
}