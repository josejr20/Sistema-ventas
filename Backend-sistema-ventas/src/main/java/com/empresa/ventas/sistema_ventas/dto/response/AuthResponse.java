package com.empresa.ventas.sistema_ventas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UsuarioInfo usuario;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioInfo {
        private Long id;
        private String uuid;
        private String nombre;
        private String apellido;
        private String email;
        private Set<String> roles;
        private Set<String> permisos;
    }
}