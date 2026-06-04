package com.empresa.ventas.sistema_ventas.mapper;

import com.empresa.ventas.sistema_ventas.dto.response.ClienteResponse;
import com.empresa.ventas.sistema_ventas.entity.cliente.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    @Mapping(source = "tipoDocumento.codigo", target = "tipoDocumentoCodigo")
    @Mapping(source = "tipoDocumento.descripcion", target = "tipoDocumentoDescripcion")
    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponseList(List<Cliente> clientes);
}