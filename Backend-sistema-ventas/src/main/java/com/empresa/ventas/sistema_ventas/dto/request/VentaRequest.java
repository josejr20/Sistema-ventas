package com.empresa.ventas.sistema_ventas.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {
    private Long clienteId;

    @NotNull(message = "El almacén es obligatorio")
    private Long almacenId;

    private String moneda = "PEN";
    private String observaciones;

    @NotNull(message = "Los detalles son obligatorios")
    @Size(min = 1, message = "Debe incluir al menos un producto")
    private List<DetalleVentaRequest> detalles;
}
