package com.empresa.ventas.sistema_ventas.controller;

import com.empresa.ventas.sistema_ventas.dto.request.ProductoRequest;
import com.empresa.ventas.sistema_ventas.dto.response.ProductoResponse;
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
    public ResponseEntity<ApiResponse<PageResponse<ProductoResponse>>> listar(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductoResponse> productos = productoService.listar(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(productos)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> buscarPorId(@PathVariable Long id) {
        ProductoResponse producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @GetMapping("/barras/{codigo}")
    public ResponseEntity<ApiResponse<ProductoResponse>> buscarPorBarras(@PathVariable String codigo) {
        ProductoResponse producto = productoService.buscarPorBarras(codigo);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = productoService.crear(request);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        ProductoResponse producto = productoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(producto));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleActivo(@PathVariable Long id) {
        productoService.toggleActivo(id);
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado", null));
    }
}