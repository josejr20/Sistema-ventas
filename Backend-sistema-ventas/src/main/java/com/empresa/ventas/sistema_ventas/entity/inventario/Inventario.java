package com.empresa.ventas.sistema_ventas.entity.inventario;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import com.empresa.ventas.sistema_ventas.entity.producto.Producto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "inventario", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"producto_id", "almacen_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(nullable = false, precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal stockActual = BigDecimal.ZERO;

    @Column(precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal stockMaximo = BigDecimal.ZERO;

    @Column
    private String ubicacion;
}