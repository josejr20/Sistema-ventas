package com.empresa.ventas.sistema_ventas.controller;

import com.empresa.ventas.sistema_ventas.dto.request.ClienteRequest;
import com.empresa.ventas.sistema_ventas.entity.cliente.Cliente;
import com.empresa.ventas.sistema_ventas.service.ClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Cliente>>> listar(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Cliente> clientes = clienteService.listar(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(clientes)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<PageResponse<Cliente>>> buscar(
            @RequestParam String termino,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Cliente> clientes = clienteService.buscar(termino, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(clientes)));
    }

    @GetMapping("/documento")
    public ResponseEntity<ApiResponse<Cliente>> buscarPorDocumento(
            @RequestParam Long tipoId,
            @RequestParam String numero) {
        Cliente cliente = clienteService.buscarPorDocumento(tipoId, numero);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> crear(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.crear(request);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }
}