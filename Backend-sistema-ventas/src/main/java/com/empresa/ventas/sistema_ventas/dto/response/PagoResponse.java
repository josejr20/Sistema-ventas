package com.empresa.ventas.sistema_ventas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponse {
    private Integer id;
    private String uuid;
    private BigDecimal monto;
    private String moneda;
    private String metodoPagoCodigo;
    private String metodoPagoDescripcion;
    private String estadoPagoCodigo;
    private String estadoPagoDescripcion;
    private String codigoAutorizacion;
    private String ultimos4Digitos;
    private String marcaTarjeta;
    private String numeroOperacion;
    private String telefonoOrigen;
    private LocalDateTime fechaPago;
}