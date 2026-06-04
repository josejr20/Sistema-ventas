package com.empresa.ventas.sistema_ventas.entity.inventario;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "almacenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Almacen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private String direccion;

    @Column(nullable = false)
    private boolean principal = false;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "almacen")
    @Builder.Default
    private Set<Inventario> inventarios = new HashSet<>();
}