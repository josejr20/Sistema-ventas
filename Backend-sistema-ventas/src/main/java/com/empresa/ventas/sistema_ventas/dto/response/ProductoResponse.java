package com.empresa.ventas.sistema_ventas.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long categoriaId;
    private String nombreCategoria;
    private Long marcaId;
    private String nombreMarca;
    private Long unidadMedidaId;
    private String nombreUnidadMedida;
    private BigDecimal stockActual;
}