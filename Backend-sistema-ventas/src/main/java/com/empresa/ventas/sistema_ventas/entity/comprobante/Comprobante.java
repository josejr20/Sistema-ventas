package com.empresa.ventas.sistema_ventas.entity.comprobante;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import com.empresa.ventas.sistema_ventas.entity.venta.Venta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class Comprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false, unique = true)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_comprobante_id", nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false, length = 4)
    private String serie;

    @Column(nullable = false)
    private int correlativo;

    @Column(unique = true, nullable = false, length = 20)
    private String numeroCompleto;

    @Column(nullable = false, length = 1)
    private String clienteTipoDoc;

    @Column(nullable = false)
    private String clienteNombreDoc;

    @Column
    private String clienteNombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(length = 20)
    private String estadoSunat = "PENDIENTE";

    @Column
    private String hashCdr;

    @Column(columnDefinition = "TEXT")
    private String xmlContent;

    @Column(columnDefinition = "TEXT")
    private String cdrContent;

    @Column
    private String pdfUrl;

    @Column
    private String qrData;

    @Column
    private String mensajeSunat;

    @Column
    private LocalDateTime fechaEnvioSunat;

    @Column
    private LocalDateTime fechaRespuestaSunat;

    @PrePersist
    public void prePersist() {
        if (fechaEnvioSunat == null) {
            fechaEnvioSunat = LocalDateTime.now();
        }
    }
}