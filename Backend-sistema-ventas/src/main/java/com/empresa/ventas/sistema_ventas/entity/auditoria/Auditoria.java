package com.empresa.ventas.sistema_ventas.entity.auditoria;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria extends BaseEntity {
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(nullable = false, length = 50)
    private String modulo;

    @Column(name = "tabla_afectada")
    private String tablaAfectada;

    @Column(name = "registro_id")
    private String registroId;

    @Column(columnDefinition = "TEXT")
    private String datosAnteriores;

    @Column(columnDefinition = "TEXT")
    private String datosNuevos;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(nullable = false)
    @Builder.Default
    private boolean exitoso = true;

    @Column
    private String mensaje;

    @PrePersist
    public void prePersist() {
    }
}