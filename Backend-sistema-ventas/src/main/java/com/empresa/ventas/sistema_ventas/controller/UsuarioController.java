package com.empresa.ventas.sistema_ventas.controller;

import com.empresa.ventas.sistema_ventas.dto.request.RegisterRequest;
import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Usuario>>> listar(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Usuario> usuarios = usuarioService.listar(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(usuarios)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Usuario>> crear(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = usuarioService.crear(request);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Usuario>> actualizar(
            @PathVariable Long id, @Valid @RequestBody RegisterRequest request) {
        Usuario usuario = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success(usuario));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleActivo(@PathVariable Long id) {
        usuarioService.toggleActivo(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario actualizado", null));
    }

    @PostMapping("/{id}/roles/{rolId}")
    public ResponseEntity<ApiResponse<Void>> asignarRol(
            @PathVariable Long id, @PathVariable Long rolId) {
        usuarioService.asignarRol(id, rolId);
        return ResponseEntity.ok(ApiResponse.success("Rol asignado correctamente", null));
    }
}