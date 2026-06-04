package com.empresa.ventas.sistema_ventas.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {
    @NotBlank(message = "El ID del tipo de documento es obligatorio")
    private Long tipoDocumentoId;

    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String apellido;
    private String razonSocial;
    private String email;
    private String telefono;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;
}
