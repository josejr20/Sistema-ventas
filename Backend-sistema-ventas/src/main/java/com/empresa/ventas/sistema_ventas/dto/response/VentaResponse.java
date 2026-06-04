package com.empresa.ventas.sistema_ventas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.empresa.ventas.sistema_ventas.entity.venta.EstadoVenta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse {
    private Integer id;
    private String uuid;
    private String numeroVenta;
    private ClienteResponse cliente;
    private VendedorResponse vendedor;
    private EstadoVenta estadoVenta;
    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String moneda;
    private LocalDateTime fechaVenta;
    private String observaciones;
    private List<DetalleVentaResponse> detalles;
    private PagoResponse pago;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendedorResponse {
        private Integer id;
        private String nombre;
        private String apellido;
    }
}
