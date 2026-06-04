package com.empresa.ventas.sistema_ventas.entity.venta;

import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import com.empresa.ventas.sistema_ventas.entity.cliente.Cliente;
import com.empresa.ventas.sistema_ventas.entity.inventario.Almacen;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ventas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"numero_venta"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta extends BaseEntity {
    @Column(unique = true, updatable = false)
    private UUID uuid;

    @Column(unique = true, nullable = false, length = 20)
    private String numeroVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_venta_id", nullable = false)
    private EstadoVenta estadoVenta;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal descuentoTotal = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal igv = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 3)
    @Builder.Default
    private String moneda = "PEN";

    @Column(precision = 8, scale = 4)
    @Builder.Default
    private BigDecimal tipoCambio = BigDecimal.ONE;

    private String observaciones;

    private LocalDateTime fechaAnulacion;

    private Long anuladoPor;

    private String motivoAnulacion;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleVenta> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}