package com.empresa.ventas.sistema_ventas.mapper;

import com.empresa.ventas.sistema_ventas.dto.response.ProductoResponse;
import com.empresa.ventas.sistema_ventas.entity.producto.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    @Mapping(source = "marca.nombre", target = "nombreMarca")
    @Mapping(source = "unidadMedida.descripcion", target = "nombreUnidadMedida")
    ProductoResponse toResponse(Producto producto);

    List<ProductoResponse> toResponseList(List<Producto> productos);
}