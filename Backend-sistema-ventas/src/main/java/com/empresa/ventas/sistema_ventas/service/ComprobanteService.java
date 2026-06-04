package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.entity.comprobante.Comprobante;
import com.empresa.ventas.sistema_ventas.entity.comprobante.TipoComprobante;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.ComprobanteRepository;
import com.empresa.ventas.sistema_ventas.repository.TipoComprobanteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;
    private final TipoComprobanteRepository tipoComprobanteRepository;

    @Transactional
    public Comprobante emitir(Venta venta) {
        TipoComprobante tipo = tipoComprobanteRepository.findTopByTipoComprobanteIdOrderByCorrelativoDesc(3L)
                .orElseGet(() -> tipoComprobanteRepository.findByCodigo("03")
                        .orElseThrow(() -> new ResourceNotFoundException("TipoComprobante", "codigo", "03")));

        int correlativo = 1;
        String serie = tipo.getSerieDefault() != null ? tipo.getSerieDefault() : "B001";

        String numeroCompleto = serie + "-" + String.format("%08d", correlativo);

        Comprobante comprobante = Comprobante.builder()
                .venta(venta)
                .tipoComprobante(tipo)
                .serie(serie)
                .correlativo(correlativo)
                .numeroCompleto(numeroCompleto)
                .subtotal(venta.getSubtotal())
                .descuento(venta.getDescuentoTotal())
                .igv(venta.getIgv())
                .total(venta.getTotal())
                .estadoSunat("PENDIENTE")
                .build();

        return comprobanteRepository.save(comprobante);
    }

    public Comprobante buscarPorVenta(Long ventaId) {
        return comprobanteRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante", "ventaId", ventaId));
    }
}