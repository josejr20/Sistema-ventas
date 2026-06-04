package com.empresa.ventas.sistema_ventas.entity.auth;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permisos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(nullable = false)
    private String modulo;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @ManyToMany(mappedBy = "permisos")
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();
}