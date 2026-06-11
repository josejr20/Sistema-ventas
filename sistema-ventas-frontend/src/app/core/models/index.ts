export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  dni?: string;
  telefono?: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  usuario: {
    id: number;
    uuid: string;
    nombre: string;
    apellido: string;
    email: string;
    roles: string[];
    permisos: string[];
  };
}

export interface TipoDocumento {
  id: number;
  codigo: string;
  descripcion: string;
}

export interface Cliente {
  id: number;
  uuid: string;
  nombre: string;
  apellido: string;
  razonSocial?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  distrito?: string;
  provincia?: string;
  departamento?: string;
  tipoDocumento?: TipoDocumento;
  numeroDocumento?: string;
  activo: boolean;
}

export interface ClienteRequest {
  tipoDocumentoId: number;
  numeroDocumento: string;
  nombre: string;
  apellido?: string;
  razonSocial?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  distrito?: string;
  provincia?: string;
  departamento?: string;
}

export interface Categoria {
  id: number;
  nombre: string;
  categoriaPadreId?: number;
}

export interface Marca {
  id: number;
  nombre: string;
  logoUrl?: string;
}

export interface UnidadMedida {
  id: number;
  codigo: string;
  descripcion: string;
  codigoSunat?: string;
}

export interface Producto {
  id: number;
  uuid: string;
  codigo?: string;
  codigoBarras?: string;
  nombre: string;
  descripcion?: string;
  imagenUrl?: string;
  precioCosto: number;
  precioVenta: number;
  precioMayoreo?: number;
  igvIncluido: boolean;
  afectoIgv: boolean;
  activo: boolean;
  destacado: boolean;
  categoria?: Categoria;
  marca?: Marca;
  unidadMedida?: UnidadMedida;
  stockActual?: number;
}

export interface ProductoRequest {
  codigo?: string;
  codigoBarras?: string;
  nombre: string;
  descripcion?: string;
  categoriaId: number;
  marcaId: number;
  unidadMedidaId: number;
  precioCosto: number;
  precioVenta: number;
  precioMayoreo?: number;
  igvIncluido?: boolean;
  afectoIgv?: boolean;
  destacado?: boolean;
}

export interface DetalleVentaRequest {
  productoId: number;
  cantidad: number;
  descuentoPorcentaje?: number;
}

export interface DetalleVentaResponse {
  id: number;
  productoNombre: string;
  productoCodigo?: string;
  cantidad: number;
  precioUnitario: number;
  descuentoPorcentaje?: number;
  descuentoMonto?: number;
  precioFinal: number;
  subtotal: number;
  igv?: number;
  total: number;
}

export interface VentaRequest {
  clienteId?: number;
  almacenId: number;
  moneda?: string;
  observaciones?: string;
  detalles: DetalleVentaRequest[];
}

export interface Vendedor {
  id: number;
  nombre: string;
  apellido: string;
}

export interface EstadoVenta {
  id: number;
  codigo: string;
  descripcion: string;
}

export interface Venta {
  id: number;
  uuid: string;
  numeroVenta: string;
  cliente?: Cliente;
  vendedor: Vendedor;
  estadoVenta: EstadoVenta;
  subtotal: number;
  descuentoTotal?: number;
  igv: number;
  total: number;
  moneda: string;
  fechaVenta: string;
  observaciones?: string;
  detalles: DetalleVentaResponse[];
  pago?: Pago;
}

export interface MetodoPago {
  id: number;
  codigo: string;
  descripcion: string;
  requierePasarela: boolean;
}

export interface EstadoPago {
  id: number;
  codigo: string;
  descripcion: string;
}

export interface Pago {
  id: number;
  uuid: string;
  monto: number;
  moneda: string;
  metodoPagoCodigo: string;
  metodoPagoDescripcion: string;
  estadoPagoCodigo: string;
  estadoPagoDescripcion: string;
  codigoAutorizacion?: string;
  ultimos4Digitos?: string;
  marcaTarjeta?: string;
  numeroOperacion?: string;
  telefonoOrigen?: string;
  fechaPago: string;
}

export interface PagoRequest {
  ventaId: number;
  metodoPagoId: number;
  monto: number;
  montoRecibido?: number;
  referenciaExterna?: string;
  telefonoOrigen?: string;
  numeroOperacion?: string;
}

export interface Almacen {
  id: number;
  nombre: string;
  direccion?: string;
  principal: boolean;
}

export interface Inventario {
  id: number;
  productoId: number;
  almacenId: number;
  stockActual: number;
  stockMinimo?: number;
  stockMaximo?: number;
  ubicacion?: string;
}

export interface TipoMovimientoStock {
  id: number;
  codigo: string;
  descripcion: string;
}

export interface TipoComprobante {
  id: number;
  codigo: string;
  descripcion: string;
  serieDefault: string;
}

export interface Comprobante {
  id: number;
  ventaId: number;
  tipoComprobante: TipoComprobante;
  serie: string;
  correlativo: string;
  numeroCompleto: string;
  clienteTipoDoc?: string;
  clienteNumeroDoc?: string;
  clienteNombre?: string;
  subtotal: number;
  igv: number;
  total: number;
  estadoSunat: string;
  pdfUrl?: string;
}

export interface Usuario {
  id: number;
  uuid: string;
  nombre: string;
  apellido: string;
  email: string;
  roles: string[];
  activo: boolean;
}
