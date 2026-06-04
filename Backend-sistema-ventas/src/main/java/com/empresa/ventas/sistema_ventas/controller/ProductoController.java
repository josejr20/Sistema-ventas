package com.empresa.ventas.sistema_ventas.controller;

import com.empresa.ventas.sistema_ventas.dto.request.ProductoRequest;
import com.empresa.ventas.sistema_ventas.entity.producto.Producto;
import com.empresa.ventas.sistema_ventas.service.ProductoService;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Producto>>> listar(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Producto> productos = productoService.listar(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(productos)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> buscarPorId(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @GetMapping("/barras/{codigo}")
    public ResponseEntity<ApiResponse<Producto>> buscarPorBarras(@PathVariable String codigo) {
        Producto producto = productoService.buscarPorBarras(codigo);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Producto>> crear(@Valid @RequestBody ProductoRequest request) {
        Producto producto = productoService.crear(request);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Producto>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        Producto producto = productoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleActivo(@PathVariable Long id) {
        productoService.toggleActivo(id);
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado", null));
    }
}