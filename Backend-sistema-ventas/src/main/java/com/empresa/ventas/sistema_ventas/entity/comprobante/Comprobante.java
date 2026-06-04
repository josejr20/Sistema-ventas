package com.empresa.ventas.sistema_ventas.entity.comprobante;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "comprobantes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tipo_comprobante_id", "serie", "correlativo"}),
    @UniqueConstraint(columnNames = {"numero_completo"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comprobante extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false, unique = true)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_comprobante_id", nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false, length = 5)
    private String serie;

    @Column(nullable = false)
    private int correlativo;

    @Column(unique = true, nullable = false, length = 20)
    private String numeroCompleto;

    private String clienteTipoDoc;

    private String clienteNumeroDoc;

    private String clienteNombre;

    private String clienteDireccion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(length = 30)
    @Builder.Default
    private String estadoSunat = "PENDIENTE";

    private String hashCdr;

    @Column(columnDefinition = "TEXT")
    private String xmlContent;

    @Column(columnDefinition = "TEXT")
    private String cdrContent;

    private String pdfUrl;

    private String qrData;
}