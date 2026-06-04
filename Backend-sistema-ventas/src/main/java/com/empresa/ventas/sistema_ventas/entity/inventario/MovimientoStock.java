package com.empresa.ventas.sistema_ventas.entity.inventario;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import com.empresa.ventas.sistema_ventas.entity.producto.Producto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "movimiento_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoStock extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_movimiento_id", nullable = false)
    private TipoMovimientoStock tipoMovimiento;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockAnterior;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockPosterior;

    @Column(name = "referencia_tipo", length = 20)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    private String motivo;

    @Column(name = "usuario_id")
    private Long usuarioId;
}