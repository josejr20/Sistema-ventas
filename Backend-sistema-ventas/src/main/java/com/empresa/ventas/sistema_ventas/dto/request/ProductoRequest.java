package com.empresa.ventas.sistema_ventas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {
    private String codigo;
    private String codigoBarras;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;
    private String imagenUrl;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "La marca es obligatoria")
    private Long marcaId;

    @NotNull(message = "La unidad de medida es obligatoria")
    private Long unidadMedidaId;

    @NotNull(message = "El precio costo es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio costo no puede ser negativo")
    private BigDecimal precioCosto;

    @NotNull(message = "El precio venta es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio venta no puede ser negativo")
    private BigDecimal precioVenta;

    @DecimalMin(value = "0.0", message = "El precio mayoreo no puede ser negativo")
    private BigDecimal precioMayoreo;

    private Boolean igvIncluido = true;
    private Boolean afectoIgv = true;
    private Boolean destacado = false;
}
