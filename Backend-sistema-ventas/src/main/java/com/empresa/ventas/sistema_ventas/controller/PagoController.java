package com.empresa.ventas.sistema_ventas.controller;

import com.empresa.ventas.sistema_ventas.dto.request.PagoRequest;
import com.empresa.ventas.sistema_ventas.entity.pago.Pago;
import com.empresa.ventas.sistema_ventas.service.PagoService;
import com.empresa.ventas.sistema_ventas.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<ApiResponse<Pago>> procesarPago(@Valid @RequestBody PagoRequest request) {
        Pago pago = pagoService.procesarPago(request);
        return ResponseEntity.ok(ApiResponse.success(pago));
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<Void>> webhook(
            @RequestBody String payload,
            @RequestHeader("X-Signature") String firma) {
        pagoService.procesarWebhook(payload, firma);
        return ResponseEntity.ok(ApiResponse.success("Webhook procesado", null));
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<ApiResponse<Pago>> buscarPorVenta(@PathVariable Long ventaId) {
        Pago pago = pagoService.buscarPorVenta(ventaId);
        return ResponseEntity.ok(ApiResponse.success(pago));
    }
}