package com.empresa.ventas.sistema_ventas.entity.producto;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "unidades_medida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 10)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Column
    private String codigoSunat;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @OneToMany(mappedBy = "unidadMedida")
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();
}