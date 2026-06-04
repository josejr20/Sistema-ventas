package com.empresa.ventas.sistema_ventas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequest {
    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    @NotNull(message = "El método de pago es obligatorio")
    private Long metodoPagoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private BigDecimal montoRecibido;
    private String referenciaExterna;
    private String telefonoOrigen;
    private String numeroOperacion;
}
