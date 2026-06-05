package com.empresa.ventas.sistema_ventas.entity.producto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "unidadMedida")
    @JsonIgnore
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();
}