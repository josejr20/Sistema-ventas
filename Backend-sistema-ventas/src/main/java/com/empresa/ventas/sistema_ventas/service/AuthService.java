package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.dto.request.LoginRequest;
import com.empresa.ventas.sistema_ventas.dto.request.RefreshTokenRequest;
import com.empresa.ventas.sistema_ventas.dto.request.RegisterRequest;
import com.empresa.ventas.sistema_ventas.dto.response.AuthResponse;
import com.empresa.ventas.sistema_ventas.entity.auth.RefreshToken;
import com.empresa.ventas.sistema_ventas.entity.auth.Rol;
import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.exception.ResourceNotFoundException;
import com.empresa.ventas.sistema_ventas.repository.RefreshTokenRepository;
import com.empresa.ventas.sistema_ventas.repository.RolRepository;
import com.empresa.ventas.sistema_ventas.repository.UsuarioRepository;
import com.empresa.ventas.sistema_ventas.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", request.getEmail()));

        usuario.setUltimoLogin(LocalDateTime.now());
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        UserDetails userDetails = usuario;
        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        RefreshToken rt = RefreshToken.builder()
                .token(refreshToken)
                .usuario(usuario)
                .expiraAt(LocalDateTime.now().plusDays(7))
                .ipAddress("")
                .build();
        refreshTokenRepository.save(rt);

        return buildAuthResponse(accessToken, refreshToken, usuario);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }

        Rol rolVendedor = rolRepository.findByNombre("VENDEDOR")
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", "VENDEDOR"));

        Usuario usuario = Usuario.builder()
                .uuid(UUID.randomUUID())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .dni(request.getDni())
                .telefono(request.getTelefono())
                .activo(true)
                .bloqueado(false)
                .intentosFallidos(0)
                .roles(Set.of(rolVendedor))
                .build();

        usuario = usuarioRepository.save(usuario);

        UserDetails userDetails = usuario;
        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        RefreshToken rt = RefreshToken.builder()
                .token(refreshToken)
                .usuario(usuario)
                .expiraAt(LocalDateTime.now().plusDays(7))
                .ipAddress("")
                .build();
        refreshTokenRepository.save(rt);

        return buildAuthResponse(accessToken, refreshToken, usuario);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken rt = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));

        if (rt.isRevocado() || rt.getExpiraAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token inválido o expirado");
        }

        Usuario usuario = rt.getUsuario();
        String accessToken = jwtTokenProvider.generateToken(usuario);

        return buildAuthResponse(accessToken, request.getRefreshToken(), usuario);
    }

    @Transactional
    public void logout(String refreshToken) {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));
        rt.setRevocado(true);
        refreshTokenRepository.save(rt);
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, Usuario usuario) {
        Set<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());
        Set<String> permisos = usuario.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900L)
                .usuario(AuthResponse.UsuarioInfo.builder()
                        .id(usuario.getId())
                        .uuid(usuario.getUuid() != null ? usuario.getUuid().toString() : null)
                        .nombre(usuario.getNombre())
                        .apellido(usuario.getApellido())
                        .email(usuario.getEmail())
                        .roles(roles)
                        .permisos(permisos)
                        .build())
                .build();
    }
}