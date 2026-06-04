package com.empresa.ventas.sistema_ventas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UsuarioInfo usuario;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioInfo {
        private Long id;
        private String uuid;
        private String nombre;
        private String apellido;
        private String email;
        private List<String> roles;
        private List<String> permisos;
    }
}
