package com.empresa.ventas.sistema_ventas.entity.venta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "estado_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @OneToMany(mappedBy = "estadoVenta")
    @JsonIgnore
    @Builder.Default
    private Set<Venta> ventas = new HashSet<>();
}