package com.empresa.ventas.sistema_ventas.controller;

import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.entity.inventario.Inventario;
import com.empresa.ventas.sistema_ventas.service.InventarioService;
import com.empresa.ventas.sistema_ventas.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    private Long getUsuarioIdAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) auth.getPrincipal();
        return usuario.getId();
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<ApiResponse<List<Inventario>>> consultarStock(@PathVariable Long id) {
        List<Inventario> inventarios = inventarioService.consultarStock(id);
        return ResponseEntity.ok(ApiResponse.success(inventarios));
    }

    @GetMapping("/alertas")
    public ResponseEntity<ApiResponse<List<Inventario>>> alertasStockBajo() {
        List<Inventario> inventarios = inventarioService.alertasStockBajo();
        return ResponseEntity.ok(ApiResponse.success(inventarios));
    }

    @PostMapping("/ajuste")
    public ResponseEntity<ApiResponse<Void>> ajustarStock(
            @RequestParam Long productoId,
            @RequestParam Long almacenId,
            @RequestParam BigDecimal cantidad,
            @RequestParam String motivo) {
        Long usuarioId = getUsuarioIdAutenticado();
        inventarioService.ajustarStock(productoId, almacenId, cantidad, motivo, usuarioId);
        return ResponseEntity.ok(ApiResponse.success("Stock ajustado correctamente", null));
    }
}