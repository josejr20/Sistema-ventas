package com.empresa.ventas.sistema_ventas.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaResponse {
    private Integer id;
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
