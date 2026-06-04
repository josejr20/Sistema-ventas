package com.empresa.ventas.sistema_ventas.dto.response;

import com.empresa.ventas.sistema_ventas.entity.pago.MetodoPago;
import com.empresa.ventas.sistema_ventas.entity.pago.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    private Long id;
    private String uuid;
    private BigDecimal monto;
    private String moneda;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;
    private String codigoAutorizacion;
    private String ultimos4Digitos;
    private String marcaTarjeta;
    private String numeroOperacion;
    private LocalDateTime fechaPago;
}
