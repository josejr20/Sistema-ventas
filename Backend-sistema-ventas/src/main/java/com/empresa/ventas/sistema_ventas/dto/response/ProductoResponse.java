package com.empresa.ventas.sistema_ventas.dto.response;

import com.empresa.ventas.sistema_ventas.entity.producto.Categoria;
import com.empresa.ventas.sistema_ventas.entity.producto.Marca;
import com.empresa.ventas.sistema_ventas.entity.producto.UnidadMedida;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private String uuid;
    private String codigo;
    private String codigoBarras;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private BigDecimal precioCosto;
    private BigDecimal precioVenta;
    private BigDecimal precioMayoreo;
    private boolean igvIncluido;
    private boolean afectoIgv;
    private boolean activo;
    private boolean destacado;
    private Categoria categoria;
    private Marca marca;
    private UnidadMedida unidadMedida;
    private BigDecimal stockActual;
}
