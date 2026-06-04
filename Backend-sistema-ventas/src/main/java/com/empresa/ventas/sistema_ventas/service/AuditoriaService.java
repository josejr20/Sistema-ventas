package com.empresa.ventas.sistema_ventas.service;

import com.empresa.ventas.sistema_ventas.entity.auditoria.Auditoria;
import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.repository.AuditoriaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void registrar(Long usuarioId, String accion, String modulo, String tablaAfectada,
                          String registroId, Object datosAnteriores, Object datosNuevos,
                          HttpServletRequest request) {
        try {
            Auditoria auditoria = Auditoria.builder()
                    .usuario(usuarioId != null ? Usuario.builder().id(usuarioId).build() : null)
                    .accion(accion)
                    .modulo(modulo)
                    .tablaAfectada(tablaAfectada)
                    .registroId(registroId)
                    .datosAnteriores(datosAnteriores != null ? objectMapper.writeValueAsString(datosAnteriores) : null)
                    .datosNuevos(datosNuevos != null ? objectMapper.writeValueAsString(datosNuevos) : null)
                    .ipAddress(getClientIp(request))
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .exitoso(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error registrando auditoría: {}", e.getMessage());
        }
    }

    public void registrarError(Long usuarioId, String accion, String modulo, String tablaAfectada,
                               String registroId, String mensaje, HttpServletRequest request) {
        try {
            Auditoria auditoria = Auditoria.builder()
                    .usuario(usuarioId != null ? Usuario.builder().id(usuarioId).build() : null)
                    .accion(accion)
                    .modulo(modulo)
                    .tablaAfectada(tablaAfectada)
                    .registroId(registroId)
                    .exitoso(false)
                    .mensaje(mensaje)
                    .ipAddress(getClientIp(request))
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error registrando auditoría de error: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String xfHeader = request.getHeader("X-Forwarded-For");
        return xfHeader != null ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }
}