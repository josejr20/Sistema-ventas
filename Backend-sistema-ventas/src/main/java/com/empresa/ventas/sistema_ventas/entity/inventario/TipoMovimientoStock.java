package com.empresa.ventas.sistema_ventas.entity.inventario;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tipo_movimiento_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoMovimientoStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @OneToMany(mappedBy = "tipoMovimiento")
    @Builder.Default
    private Set<MovimientoStock> movimientos = new HashSet<>();
}