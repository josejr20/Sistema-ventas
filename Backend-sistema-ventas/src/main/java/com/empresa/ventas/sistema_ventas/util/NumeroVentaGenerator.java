package com.empresa.ventas.sistema_ventas.util;

import com.empresa.ventas.sistema_ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NumeroVentaGenerator {
    private final VentaRepository ventaRepository;

    public synchronized String generarNumeroVenta() {
        String ultimoNumero = ventaRepository.findMaxNumeroVenta();
        if (ultimoNumero == null) {
            return String.format("VEN-%08d", 1);
        }
        try {
            long numero = Long.parseLong(ultimoNumero.substring(4));
            return String.format("VEN-%08d", numero + 1);
        } catch (NumberFormatException e) {
            log.error("Error al parsear número de venta: {}", ultimoNumero);
            return String.format("VEN-%08d", 1);
        }
    }
}