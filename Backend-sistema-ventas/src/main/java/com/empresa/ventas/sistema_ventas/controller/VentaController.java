package com.empresa.ventas.sistema_ventas.controller;

import com.empresa.ventas.sistema_ventas.dto.request.PagoRequest;
import com.empresa.ventas.sistema_ventas.dto.request.VentaRequest;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import com.empresa.ventas.sistema_ventas.service.VentaService;
import com.empresa.ventas.sistema_ventas.util.ApiResponse;
import com.empresa.ventas.sistema_ventas.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Venta>>> listar(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Venta> ventas = ventaService.listar(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(ventas)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Venta>> buscarPorId(@PathVariable Long id) {
        Venta venta = ventaService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.success(venta));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Venta>> crear(@Valid @RequestBody VentaRequest request) {
        Venta venta = ventaService.crearVenta(request, 1L);
        return ResponseEntity.ok(ApiResponse.success(venta));
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<ApiResponse<Venta>> anular(
            @PathVariable Long id, @RequestParam String motivo) {
        Venta venta = ventaService.anularVenta(id, motivo, 1L);
        return ResponseEntity.ok(ApiResponse.success(venta));
    }

    @PostMapping("/{id}/pago")
    public ResponseEntity<ApiResponse<Venta>> confirmarPago(
            @PathVariable Long id, @Valid @RequestBody PagoRequest request) {
        Venta venta = ventaService.confirmarPago(id, request);
        return ResponseEntity.ok(ApiResponse.success(venta));
    }
}