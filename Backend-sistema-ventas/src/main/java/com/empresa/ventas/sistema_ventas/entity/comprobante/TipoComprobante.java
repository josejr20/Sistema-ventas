package com.empresa.ventas.sistema_ventas.entity.comprobante;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tipo_comprobante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoComprobante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 2)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "serie_default")
    private String serieDefault;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "tipoComprobante")
    @Builder.Default
    private Set<Comprobante> comprobantes = new HashSet<>();
}