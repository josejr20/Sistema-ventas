package com.empresa.ventas.sistema_ventas.entity.pago;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago extends BaseEntity {
    @Column(unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_pago_id", nullable = false)
    private MetodoPago metodoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_pago_id", nullable = false)
    private EstadoPago estadoPago;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(length = 3)
    @Builder.Default
    private String moneda = "PEN";

    @Column(precision = 12, scale = 2)
    private BigDecimal montoRecibido;

    @Column(precision = 12, scale = 2)
    private BigDecimal vuelto;

    @Column(name = "referencia_pasarela")
    private String referenciaPasarela;

    @Column(name = "codigo_autorizacion")
    private String codigoAutorizacion;

    @Column(name = "ultimos_4_digitos", length = 4)
    private String ultimos4Digitos;

    @Column(name = "marca_tarjeta")
    private String marcaTarjeta;

    @Column(name = "numero_operacion")
    private String numeroOperacion;

    @Column(name = "telefono_origen")
    private String telefonoOrigen;

    private String observaciones;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}