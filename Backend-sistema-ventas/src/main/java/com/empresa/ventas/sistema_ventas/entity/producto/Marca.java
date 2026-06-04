package com.empresa.ventas.sistema_ventas.entity.producto;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "marcas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private String logoUrl;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "marca")
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();
}