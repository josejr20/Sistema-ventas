package com.empresa.ventas.sistema_ventas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaResponse {
    private Long id;
    private String productoNombre;
    private String productoCodigo;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoMonto;
    private BigDecimal precioFinal;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
}
