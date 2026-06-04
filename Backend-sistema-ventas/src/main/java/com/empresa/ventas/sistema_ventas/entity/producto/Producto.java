package com.empresa.ventas.sistema_ventas.entity.producto;

import com.empresa.ventas.sistema_ventas.entity.base.BaseEntity;
import com.empresa.ventas.sistema_ventas.entity.auth.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "productos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"}),
    @UniqueConstraint(columnNames = {"codigo_barras"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto extends BaseEntity {
    @Column(unique = true, updatable = false)
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(unique = true)
    private String codigoBarras;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private String imagenUrl;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal precioCosto;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal precioVenta;

    @Column(precision = 12, scale = 2)
    private BigDecimal precioMayoreo;

    @Column(nullable = false)
    @Builder.Default
    private boolean igvIncluido = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean afectoIgv = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean destacado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadMedida unidadMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Usuario createdBy;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<com.empresa.ventas.sistema_ventas.entity.inventario.Inventario> inventarios = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}