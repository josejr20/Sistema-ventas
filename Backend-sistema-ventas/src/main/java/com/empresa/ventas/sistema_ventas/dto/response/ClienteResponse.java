package com.empresa.ventas.sistema_ventas.dto.response;

import com.empresa.ventas.sistema_ventas.entity.cliente.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Integer id;
    private String uuid;
    private String nombre;
    private String apellido;
    private String razonSocial;
    private String email;
    private String telefono;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private boolean activo;
}
