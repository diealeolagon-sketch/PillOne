package com.pillone.pillone.controller;

import com.pillone.pillone.model.*;
import com.pillone.pillone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    @Autowired private VentasRepository ventasRepository;
    @Autowired private DetallesVentasRepository detallesVentasRepository;
    @Autowired private ProductosRepository productosRepository;
    @Autowired private ClientesRepository clientesRepository;
    @Autowired private EmpleadosRepository empleadosRepository;
    @Autowired private SucursalesRepository sucursalesRepository;
    @Autowired private ConfiguracionRepository configuracionRepository;
    @Autowired private LotesRepository lotesRepository;
    @Autowired private DomiciliosRepository domiciliosRepository;

    private static final Set<String> METODOS_PAGO = Set.of(
            "EFECTIVO","TARJETA_DEBITO","TARJETA_CREDITO","TRANSFERENCIA","NEQUI_DAVIPLATA","PAGO_MIXTO"
    );

    @GetMapping
    public List<Ventas> listar() {
        return ventasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ventas> obtener(@PathVariable Long id) {
        return ventasRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String,Object>> crearVenta(@RequestBody VentaRequest request) {
        validarRequest(request);

        Sucursales sucursal = sucursalesRepository.findById(request.getIdSucursal())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        Empleados empleado = empleadosRepository.findById(request.getIdEmpleado())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        Clientes cliente = null;
        if (request.getIdCliente() != null) {
            cliente = clientesRepository.findById(request.getIdCliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        }

        String metodoPago = normalizarMetodoPago(request.getMetodoPago());
        BigDecimal subtotal = BigDecimal.ZERO;
        List<ItemCalculado> itemsCalculados = new ArrayList<>();

        for (ItemVentaRequest item : request.getItems()) {
            if (item.getIdProducto() == null) throw new RuntimeException("Hay un producto sin identificador");
            if (item.getCantidad() == null || item.getCantidad() <= 0) throw new RuntimeException("La cantidad debe ser mayor que cero");

            Productos producto = productosRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getIdProducto()));

            if (producto.getEstado() != null && !"ACTIVO".equalsIgnoreCase(producto.getEstado())) {
                throw new RuntimeException("El producto " + producto.getNombreComercial() + " no está activo");
            }

            PrecioVenta precio = calcularPrecio(producto, item.getTipoVenta());
            int unidadesNecesarias = item.getCantidad() * precio.factor();
            int stockActual = producto.getStockTotal() == null ? 0 : producto.getStockTotal();

            if (stockActual < unidadesNecesarias) {
                throw new RuntimeException("Stock insuficiente de " + producto.getNombreComercial() + ". Disponible: " + stockActual + " unidades");
            }

            validarDisponibilidadPorLotes(producto, precio.factor(), item.getCantidad());

            BigDecimal subtotalLinea = precio.precio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(subtotalLinea);
            itemsCalculados.add(new ItemCalculado(producto, normalizarTipo(item.getTipoVenta()), item.getCantidad(), precio));
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal descuento = request.getDescuento() == null ? BigDecimal.ZERO : request.getDescuento();
        if (descuento.compareTo(BigDecimal.ZERO) < 0) descuento = BigDecimal.ZERO;
        if (descuento.compareTo(subtotal) > 0) descuento = subtotal;
        descuento = descuento.setScale(2, RoundingMode.HALF_UP);

        ConfiguracionGlobal config = configuracionRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("No existe la configuración global del sistema"));
        BigDecimal porcentajeIva = config.getIvaGeneral() == null ? BigDecimal.ZERO : config.getIvaGeneral();
        boolean aplicaIva = request.getAplicaIva() == null || request.getAplicaIva();
        BigDecimal base = subtotal.subtract(descuento);
        BigDecimal impuestoIva = aplicaIva
                ? base.multiply(porcentajeIva).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal costoDomicilio = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(request.getEsDomicilio())) {
            if (cliente == null) throw new RuntimeException("Para un domicilio debe seleccionar un cliente");
            if (request.getDireccionEntrega() == null || request.getDireccionEntrega().trim().isEmpty()) {
                throw new RuntimeException("Debe ingresar la dirección de entrega");
            }
            if (request.getTelefonoContacto() == null || request.getTelefonoContacto().trim().isEmpty()) {
                throw new RuntimeException("Debe ingresar el teléfono de contacto");
            }
            costoDomicilio = request.getCostoDomicilio() == null ? BigDecimal.ZERO : request.getCostoDomicilio();
            if (costoDomicilio.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("El costo del domicilio no puede ser negativo");
            }
            costoDomicilio = costoDomicilio.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal total = base.add(impuestoIva).add(costoDomicilio).setScale(2, RoundingMode.HALF_UP);

        Ventas venta = new Ventas();
        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);
        venta.setNumeroFactura(generarNumeroFactura());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setSubtotal(subtotal);
        venta.setDescuento(descuento);
        venta.setAplicaIva(aplicaIva);
        venta.setPorcentajeIva(porcentajeIva);
        venta.setImpuestoIva(impuestoIva);
        venta.setTotal(total);
        venta.setMetodoPago(metodoPago);
        venta.setEstado("PAGADA");
        venta = ventasRepository.save(venta);

        for (ItemCalculado item : itemsCalculados) {
            descontarPorFEFOYGuardarDetalles(venta, item);
        }

        if (Boolean.TRUE.equals(request.getEsDomicilio())) {
            Domicilios domicilio = new Domicilios();
            domicilio.setVenta(venta);
            domicilio.setCliente(cliente);
            domicilio.setDireccionEntrega(request.getDireccionEntrega().trim());
            domicilio.setTelefonoContacto(request.getTelefonoContacto().trim());
            domicilio.setNombreDomiciliario(
                    request.getNombreDomiciliario() == null || request.getNombreDomiciliario().trim().isEmpty()
                            ? null
                            : request.getNombreDomiciliario().trim()
            );
            domicilio.setCostoDomicilio(costoDomicilio);
            domicilio.setEstado("PENDIENTE");
            domiciliosRepository.save(domicilio);
        }

        Map<String,Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idVenta", venta.getIdVenta());
        respuesta.put("numeroFactura", venta.getNumeroFactura());
        respuesta.put("subtotal", venta.getSubtotal());
        respuesta.put("descuento", venta.getDescuento());
        respuesta.put("aplicaIva", venta.getAplicaIva());
        respuesta.put("porcentajeIva", venta.getPorcentajeIva());
        respuesta.put("impuestoIva", venta.getImpuestoIva());
        respuesta.put("esDomicilio", Boolean.TRUE.equals(request.getEsDomicilio()));
        respuesta.put("costoDomicilio", costoDomicilio);
        respuesta.put("total", venta.getTotal());
        respuesta.put("mensaje", Boolean.TRUE.equals(request.getEsDomicilio())
                ? "Venta y domicilio registrados correctamente"
                : "Venta registrada correctamente");
        return ResponseEntity.ok(respuesta);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> manejarError(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    private void validarRequest(VentaRequest request) {
        if (request == null) throw new RuntimeException("Solicitud de venta vacía");
        if (request.getIdSucursal() == null) throw new RuntimeException("Debe seleccionar una sucursal");
        if (request.getIdEmpleado() == null) throw new RuntimeException("Debe seleccionar un empleado");
        if (request.getItems() == null || request.getItems().isEmpty()) throw new RuntimeException("Debe agregar al menos un producto");
    }

    private String normalizarMetodoPago(String metodoPago) {
        String valor = metodoPago == null ? "EFECTIVO" : metodoPago.trim().toUpperCase();
        if (!METODOS_PAGO.contains(valor)) throw new RuntimeException("Método de pago no válido");
        return valor;
    }

    private String normalizarTipo(String tipoVenta) {
        String tipo = tipoVenta == null ? "UNIDAD" : tipoVenta.trim().toUpperCase();
        if (!Set.of("UNIDAD","SELLO","CAJA").contains(tipo)) throw new RuntimeException("Tipo de venta no válido: " + tipo);
        return tipo;
    }

    private PrecioVenta calcularPrecio(Productos producto, String tipoVenta) {
        String tipo = normalizarTipo(tipoVenta);
        BigDecimal precioUnidad = BigDecimal.valueOf(producto.getPrecioVenta() == null ? 0D : producto.getPrecioVenta());
        int unidadesPorSello = producto.getUnidadesPorSello() == null || producto.getUnidadesPorSello() <= 0 ? 1 : producto.getUnidadesPorSello();
        int unidadesPorEmpaque = producto.getUnidadesPorEmpaque() == null || producto.getUnidadesPorEmpaque() <= 0
                ? Math.max(1, (producto.getSellosPorCaja() == null ? 1 : producto.getSellosPorCaja()) * unidadesPorSello)
                : producto.getUnidadesPorEmpaque();

        return switch (tipo) {
            case "SELLO" -> new PrecioVenta(unidadesPorSello,
                    precioUnidad.multiply(BigDecimal.valueOf(unidadesPorSello)).setScale(2, RoundingMode.HALF_UP));
            case "CAJA" -> {
                BigDecimal precioCaja = producto.getPrecioVentaEmpaque() != null && producto.getPrecioVentaEmpaque() > 0
                        ? BigDecimal.valueOf(producto.getPrecioVentaEmpaque())
                        : precioUnidad.multiply(BigDecimal.valueOf(unidadesPorEmpaque));
                yield new PrecioVenta(unidadesPorEmpaque, precioCaja.setScale(2, RoundingMode.HALF_UP));
            }
            default -> new PrecioVenta(1, precioUnidad.setScale(2, RoundingMode.HALF_UP));
        };
    }

    private void validarDisponibilidadPorLotes(Productos producto, int factor, int cantidadPresentaciones) {
        List<Lotes> lotes = lotesRepository.buscarLotesFEFO(producto.getIdProducto(), LocalDate.now());
        if (lotes.isEmpty()) throw new RuntimeException("No hay lotes vigentes disponibles para " + producto.getNombreComercial());

        List<Integer> saldos = lotes.stream().map(l -> l.getCantidadActual() == null ? 0 : l.getCantidadActual()).toList();
        boolean[] usados = new boolean[saldos.size()];
        int[] restante = saldos.stream().mapToInt(Integer::intValue).toArray();

        for (int i = 0; i < cantidadPresentaciones; i++) {
            boolean encontrado = false;
            for (int j = 0; j < restante.length; j++) {
                if (restante[j] >= factor) {
                    restante[j] -= factor;
                    usados[j] = true;
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                throw new RuntimeException("No hay suficiente stock por lotes vigentes para vender " + cantidadPresentaciones + " " + (factor == 1 ? "unidad(es)" : "presentación(es)") + " de " + producto.getNombreComercial());
            }
        }
    }

    private void descontarPorFEFOYGuardarDetalles(Ventas venta, ItemCalculado item) {
        Productos producto = item.producto();
        int factor = item.precio().factor();
        List<Lotes> lotes = lotesRepository.buscarLotesFEFO(producto.getIdProducto(), LocalDate.now());

        Map<Long,Integer> presentacionesPorLote = new LinkedHashMap<>();
        Map<Long,Lotes> lotePorId = new LinkedHashMap<>();

        for (int i = 0; i < item.cantidad(); i++) {
            Lotes elegido = null;
            for (Lotes lote : lotes) {
                int disponible = lote.getCantidadActual() == null ? 0 : lote.getCantidadActual();
                if (disponible >= factor) {
                    elegido = lote;
                    break;
                }
            }
            if (elegido == null) throw new RuntimeException("Stock por lotes insuficiente para " + producto.getNombreComercial());
            elegido.setCantidadActual(elegido.getCantidadActual() - factor);
            presentacionesPorLote.merge(elegido.getIdLote(), 1, Integer::sum);
            lotePorId.put(elegido.getIdLote(), elegido);
        }

        for (Lotes lote : lotePorId.values()) lotesRepository.save(lote);

        for (Map.Entry<Long,Integer> entry : presentacionesPorLote.entrySet()) {
            int cantidadPresentaciones = entry.getValue();
            DetallesVentas detalle = new DetallesVentas();
            detalle.setVenta(venta);
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setIdLote(entry.getKey());
            detalle.setTipoVenta(item.tipoVenta());
            detalle.setCantidad(cantidadPresentaciones);
            detalle.setUnidadesDescontadas(cantidadPresentaciones * factor);
            detalle.setPrecioUnitario(item.precio().precio());
            detalle.setSubtotal(item.precio().precio().multiply(BigDecimal.valueOf(cantidadPresentaciones)).setScale(2, RoundingMode.HALF_UP));
            detallesVentasRepository.save(detalle);
        }

        int stockActual = producto.getStockTotal() == null ? 0 : producto.getStockTotal();
        producto.setStockTotal(stockActual - (item.cantidad() * factor));
        productosRepository.save(producto);
    }

    private String generarNumeroFactura() {
        return "FARM-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private record PrecioVenta(int factor, BigDecimal precio) {}
    private record ItemCalculado(Productos producto, String tipoVenta, int cantidad, PrecioVenta precio) {}

    public static class VentaRequest {
        private Long idSucursal;
        private Long idCliente;
        private Long idEmpleado;
        private BigDecimal descuento;
        private Boolean aplicaIva = true;
        private String metodoPago;
        private Boolean esDomicilio = false;
        private String direccionEntrega;
        private String telefonoContacto;
        private String nombreDomiciliario;
        private BigDecimal costoDomicilio = BigDecimal.ZERO;
        private List<ItemVentaRequest> items;

        public Long getIdSucursal() { return idSucursal; }
        public void setIdSucursal(Long idSucursal) { this.idSucursal = idSucursal; }
        public Long getIdCliente() { return idCliente; }
        public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
        public Long getIdEmpleado() { return idEmpleado; }
        public void setIdEmpleado(Long idEmpleado) { this.idEmpleado = idEmpleado; }
        public BigDecimal getDescuento() { return descuento; }
        public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
        public Boolean getAplicaIva() { return aplicaIva; }
        public void setAplicaIva(Boolean aplicaIva) { this.aplicaIva = aplicaIva; }
        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
        public Boolean getEsDomicilio() { return esDomicilio; }
        public void setEsDomicilio(Boolean esDomicilio) { this.esDomicilio = esDomicilio; }
        public String getDireccionEntrega() { return direccionEntrega; }
        public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
        public String getTelefonoContacto() { return telefonoContacto; }
        public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }
        public String getNombreDomiciliario() { return nombreDomiciliario; }
        public void setNombreDomiciliario(String nombreDomiciliario) { this.nombreDomiciliario = nombreDomiciliario; }
        public BigDecimal getCostoDomicilio() { return costoDomicilio; }
        public void setCostoDomicilio(BigDecimal costoDomicilio) { this.costoDomicilio = costoDomicilio; }
        public List<ItemVentaRequest> getItems() { return items; }
        public void setItems(List<ItemVentaRequest> items) { this.items = items; }
    }

    public static class ItemVentaRequest {
        private Long idProducto;
        private String tipoVenta = "UNIDAD";
        private Integer cantidad = 1;

        public Long getIdProducto() { return idProducto; }
        public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
        public String getTipoVenta() { return tipoVenta; }
        public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
