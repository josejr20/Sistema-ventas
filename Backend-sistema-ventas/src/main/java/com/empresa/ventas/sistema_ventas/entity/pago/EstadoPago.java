package com.empresa.ventas.sistema_ventas.entity.pago;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "estado_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @OneToMany(mappedBy = "estadoPago")
    @JsonIgnore
    @Builder.Default
    private Set<Pago> pagos = new HashSet<>();
}
