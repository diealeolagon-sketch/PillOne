package com.pillone.pillone.controller;

import com.pillone.pillone.model.*;
import com.pillone.pillone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

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
    @Autowired private FormulasMedicasRepository formulasMedicasRepository;
    @Autowired private DetallesFormulasRepository detallesFormulasRepository;
    @Autowired private UsuariosRepository usuariosRepository;

    private static final Set<String> METODOS_PAGO=Set.of(
            "EFECTIVO","TARJETA_DEBITO","TARJETA_CREDITO","TRANSFERENCIA","NEQUI_DAVIPLATA","PAGO_MIXTO"
    );

    @GetMapping
    public List<Ventas> listar(){
        return ventasRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ventas> obtener(@PathVariable Long id){
        return ventasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/formulas-disponibles")
    public ResponseEntity<List<Map<String,Object>>> formulasDisponibles(
            @RequestParam Long clienteId,
            @RequestParam Long productoId
    ){
        List<Map<String,Object>> respuesta=new ArrayList<>();
        LocalDate hoy=LocalDate.now();

        for(FormulasMedicas formula:formulasMedicasRepository.findByIdCliente(clienteId)){
            if(!formula.isVigente(hoy)){
                continue;
            }

            if(!detallesFormulasRepository.existsByIdFormulaAndIdProducto(
                    formula.getIdFormula(),
                    productoId
            )){
                continue;
            }

            Map<String,Object> item=new LinkedHashMap<>();
            item.put("idFormula",formula.getIdFormula());
            item.put("nombreMedico",formula.getNombreMedico());
            item.put("entidadSalud",formula.getEntidadSalud());
            item.put("fechaExpedicion",formula.getFechaExpedicion());
            item.put("fechaVencimiento",formula.getFechaVencimiento());
            item.put("vigenciaDias",formula.getVigenciaDias());
            respuesta.add(item);
        }

        respuesta.sort(
                Comparator.comparing(
                        m -> (LocalDate)m.get("fechaExpedicion"),
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String,Object>> crearVenta(@RequestBody VentaRequest request, HttpSession session){
        validarRequest(request);

        Sucursales sucursal=sucursalesRepository.findById(request.getIdSucursal())
                .orElseThrow(()->new RuntimeException("Sucursal no encontrada"));

        Object idUsuarioSesion=session.getAttribute("idUsuario");
        if(!(idUsuarioSesion instanceof Number)){
            throw new RuntimeException("La sesión no tiene un usuario válido");
        }
        Usuarios usuario=usuariosRepository.findById(((Number)idUsuarioSesion).longValue())
                .orElseThrow(()->new RuntimeException("Usuario autenticado no encontrado"));
        Empleados empleado=empleadosRepository.findById(usuario.getIdEmpleado())
                .orElseThrow(()->new RuntimeException("Empleado asociado al usuario no encontrado"));

        Clientes cliente=null;

        if(request.getIdCliente()!=null){
            cliente=clientesRepository.findById(request.getIdCliente())
                    .orElseThrow(()->new RuntimeException("Cliente no encontrado"));
        }

        String metodoPago=normalizarMetodoPago(request.getMetodoPago());

        BigDecimal subtotal=BigDecimal.ZERO;
        BigDecimal ivaIncluidoTotal=BigDecimal.ZERO;
        List<ItemCalculado> itemsCalculados=new ArrayList<>();

        for(ItemVentaRequest item:request.getItems()){
            if(item.getIdProducto()==null){
                throw new RuntimeException("Hay un producto sin identificador");
            }

            if(item.getCantidad()==null||item.getCantidad()<=0){
                throw new RuntimeException("La cantidad debe ser mayor que cero");
            }

            Productos producto=productosRepository.findById(item.getIdProducto())
                    .orElseThrow(()->new RuntimeException("Producto no encontrado: "+item.getIdProducto()));

            if(producto.getEstado()!=null&&!"ACTIVO".equalsIgnoreCase(producto.getEstado())){
                throw new RuntimeException("El producto "+producto.getNombreComercial()+" no está activo");
            }

            boolean exigeFormula=
                    Boolean.TRUE.equals(producto.getRequiereFormula())
                            ||
                            Boolean.TRUE.equals(producto.getEsControlado());

            Long idFormulaSeleccionada=null;

            if(exigeFormula){
                if(cliente==null){
                    throw new RuntimeException(
                            "El producto "+producto.getNombreComercial()+
                                    " requiere fórmula médica. Debe seleccionar un cliente registrado."
                    );
                }

                FormulasMedicas formula=validarFormulaSeleccionada(
                        item.getIdFormula(),
                        request.getIdCliente(),
                        producto.getIdProducto(),
                        producto.getNombreComercial()
                );

                idFormulaSeleccionada=formula.getIdFormula();
            }

            PrecioVenta precio=calcularPrecio(producto,item.getTipoVenta());

            int unidadesNecesarias=item.getCantidad()*precio.factor();
            int stockActual=producto.getStockTotal()==null?0:producto.getStockTotal();

            if(stockActual<unidadesNecesarias){
                throw new RuntimeException(
                        "Stock insuficiente de "+producto.getNombreComercial()+
                                ". Disponible: "+stockActual+" unidades"
                );
            }

            validarDisponibilidadPorLotes(
                    producto,
                    precio.factor(),
                    item.getCantidad()
            );

            BigDecimal subtotalLinea=precio.precio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()))
                    .setScale(2,RoundingMode.HALF_UP);

            subtotal=subtotal.add(subtotalLinea);

            BigDecimal tasaIva=producto.getPorcentajeIva()==null?BigDecimal.ZERO:producto.getPorcentajeIva();
            if(tasaIva.signum()>0){
                BigDecimal baseLinea=subtotalLinea.divide(BigDecimal.ONE.add(tasaIva.divide(new BigDecimal("100"),8,RoundingMode.HALF_UP)),8,RoundingMode.HALF_UP);
                ivaIncluidoTotal=ivaIncluidoTotal.add(subtotalLinea.subtract(baseLinea));
            }

            itemsCalculados.add(
                    new ItemCalculado(
                            producto,
                            normalizarTipo(item.getTipoVenta()),
                            item.getCantidad(),
                            precio,
                            idFormulaSeleccionada
                    )
            );
        }

        subtotal=subtotal.setScale(2,RoundingMode.HALF_UP);

        BigDecimal descuento=
                request.getDescuento()==null
                        ? BigDecimal.ZERO
                        : request.getDescuento();

        if(descuento.compareTo(BigDecimal.ZERO)<0){
            descuento=BigDecimal.ZERO;
        }

        if(descuento.compareTo(subtotal)>0){
            descuento=subtotal;
        }

        descuento=descuento.setScale(2,RoundingMode.HALF_UP);

        BigDecimal base=subtotal.subtract(descuento).setScale(2,RoundingMode.HALF_UP);
        BigDecimal factorDescuento=subtotal.signum()>0
                ? base.divide(subtotal,8,RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal impuestoIva=ivaIncluidoTotal.multiply(factorDescuento).setScale(2,RoundingMode.HALF_UP);
        boolean aplicaIva=impuestoIva.signum()>0;
        BigDecimal porcentajeIva=BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP);

        BigDecimal costoDomicilio=BigDecimal.ZERO;

        if(Boolean.TRUE.equals(request.getEsDomicilio())){
            if(cliente==null){
                throw new RuntimeException("Para un domicilio debe seleccionar un cliente");
            }

            if(request.getDireccionEntrega()==null||request.getDireccionEntrega().trim().isEmpty()){
                throw new RuntimeException("Debe ingresar la dirección de entrega");
            }

            if(request.getTelefonoContacto()==null||request.getTelefonoContacto().trim().isEmpty()){
                throw new RuntimeException("Debe ingresar el teléfono de contacto");
            }

            costoDomicilio=
                    request.getCostoDomicilio()==null
                            ? BigDecimal.ZERO
                            : request.getCostoDomicilio();

            if(costoDomicilio.compareTo(BigDecimal.ZERO)<0){
                throw new RuntimeException("El costo del domicilio no puede ser negativo");
            }

            costoDomicilio=costoDomicilio.setScale(2,RoundingMode.HALF_UP);
        }

        BigDecimal total=
                base.add(costoDomicilio)
                        .setScale(2,RoundingMode.HALF_UP);

        Ventas venta=new Ventas();

        venta.setSucursal(sucursal);
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);
        venta.setUsuario(usuario);
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

        venta=ventasRepository.save(venta);

        for(ItemCalculado item:itemsCalculados){
            descontarPorFEFOYGuardarDetalles(venta,item);
        }

        if(Boolean.TRUE.equals(request.getEsDomicilio())){
            Domicilios domicilio=new Domicilios();

            domicilio.setVenta(venta);
            domicilio.setCliente(cliente);
            domicilio.setDireccionEntrega(request.getDireccionEntrega().trim());
            domicilio.setTelefonoContacto(request.getTelefonoContacto().trim());

            domicilio.setNombreDomiciliario(
                    request.getNombreDomiciliario()==null
                            ||
                            request.getNombreDomiciliario().trim().isEmpty()
                            ? null
                            : request.getNombreDomiciliario().trim()
            );

            domicilio.setCostoDomicilio(costoDomicilio);
            domicilio.setEstado("PENDIENTE");

            domiciliosRepository.save(domicilio);
        }

        Map<String,Object> respuesta=new LinkedHashMap<>();

        respuesta.put("idVenta",venta.getIdVenta());
        respuesta.put("numeroFactura",venta.getNumeroFactura());
        respuesta.put("subtotal",venta.getSubtotal());
        respuesta.put("descuento",venta.getDescuento());
        respuesta.put("aplicaIva",venta.getAplicaIva());
        respuesta.put("porcentajeIva",venta.getPorcentajeIva());
        respuesta.put("impuestoIva",venta.getImpuestoIva());
        respuesta.put("esDomicilio",Boolean.TRUE.equals(request.getEsDomicilio()));
        respuesta.put("costoDomicilio",costoDomicilio);
        respuesta.put("total",venta.getTotal());

        respuesta.put(
                "mensaje",
                Boolean.TRUE.equals(request.getEsDomicilio())
                        ? "Venta y domicilio registrados correctamente"
                        : "Venta registrada correctamente"
        );

        return ResponseEntity.ok(respuesta);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> manejarError(RuntimeException e){
        return ResponseEntity.badRequest()
                .body(Map.of("error",e.getMessage()));
    }

    private void validarRequest(VentaRequest request){
        if(request==null){
            throw new RuntimeException("Solicitud de venta vacía");
        }

        if(request.getIdSucursal()==null){
            throw new RuntimeException("Debe seleccionar una sucursal");
        }

        if(request.getItems()==null||request.getItems().isEmpty()){
            throw new RuntimeException("Debe agregar al menos un producto");
        }
    }

    private String normalizarMetodoPago(String metodoPago){
        String valor=
                metodoPago==null
                        ? "EFECTIVO"
                        : metodoPago.trim().toUpperCase();

        if(!METODOS_PAGO.contains(valor)){
            throw new RuntimeException("Método de pago no válido");
        }

        return valor;
    }

    private String normalizarTipo(String tipoVenta){
        String tipo=
                tipoVenta==null
                        ? "UNIDAD"
                        : tipoVenta.trim().toUpperCase();

        if(!Set.of("UNIDAD","SELLO","CAJA").contains(tipo)){
            throw new RuntimeException("Tipo de venta no válido: "+tipo);
        }

        return tipo;
    }

    private PrecioVenta calcularPrecio(Productos producto,String tipoVenta){
        String tipo=normalizarTipo(tipoVenta);

        BigDecimal precioUnidad=
                BigDecimal.valueOf(
                        producto.getPrecioVenta()==null
                                ? 0D
                                : producto.getPrecioVenta()
                );

        int unidadesPorSello=
                producto.getUnidadesPorSello()==null
                        ||
                        producto.getUnidadesPorSello()<=0
                        ? 1
                        : producto.getUnidadesPorSello();

        int unidadesPorEmpaque=
                producto.getUnidadesPorEmpaque()==null
                        ||
                        producto.getUnidadesPorEmpaque()<=0
                        ? Math.max(
                        1,
                        (producto.getSellosPorCaja()==null
                                ? 1
                                : producto.getSellosPorCaja())
                                *
                                unidadesPorSello
                )
                        : producto.getUnidadesPorEmpaque();

        return switch(tipo){
            case "SELLO" ->
                    new PrecioVenta(
                            unidadesPorSello,
                            precioUnidad
                                    .multiply(BigDecimal.valueOf(unidadesPorSello))
                                    .setScale(2,RoundingMode.HALF_UP)
                    );

            case "CAJA" -> {
                BigDecimal precioCaja=
                        producto.getPrecioVentaEmpaque()!=null
                                &&
                                producto.getPrecioVentaEmpaque()>0
                                ? BigDecimal.valueOf(producto.getPrecioVentaEmpaque())
                                : precioUnidad.multiply(BigDecimal.valueOf(unidadesPorEmpaque));

                yield new PrecioVenta(
                        unidadesPorEmpaque,
                        precioCaja.setScale(2,RoundingMode.HALF_UP)
                );
            }

            default ->
                    new PrecioVenta(
                            1,
                            precioUnidad.setScale(2,RoundingMode.HALF_UP)
                    );
        };
    }

    private FormulasMedicas validarFormulaSeleccionada(
            Long idFormula,
            Long idCliente,
            Long idProducto,
            String nombreProducto
    ){
        if(idFormula==null){
            throw new RuntimeException(
                    "Debe seleccionar una fórmula médica vigente para "+nombreProducto+"."
            );
        }

        FormulasMedicas formula=formulasMedicasRepository.findById(idFormula)
                .orElseThrow(
                        ()->new RuntimeException("La fórmula médica seleccionada no existe.")
                );

        if(!Objects.equals(formula.getIdCliente(),idCliente)){
            throw new RuntimeException(
                    "La fórmula seleccionada no pertenece al cliente de esta venta."
            );
        }

        if(!formula.isVigente(LocalDate.now())){
            throw new RuntimeException(
                    "La fórmula seleccionada está vencida o todavía no está vigente."
            );
        }

        if(!detallesFormulasRepository.existsByIdFormulaAndIdProducto(
                formula.getIdFormula(),
                idProducto
        )){
            throw new RuntimeException(
                    "La fórmula seleccionada no autoriza la venta de "+nombreProducto+"."
            );
        }

        return formula;
    }

    private void validarDisponibilidadPorLotes(
            Productos producto,
            int factor,
            int cantidadPresentaciones
    ){
        List<Lotes> lotes=
                lotesRepository.buscarLotesFEFO(
                        producto.getIdProducto(),
                        LocalDate.now()
                );

        if(lotes.isEmpty()){
            throw new RuntimeException(
                    "No hay lotes vigentes disponibles para "+
                            producto.getNombreComercial()
            );
        }

        int[] restante=
                lotes.stream()
                        .mapToInt(
                                l ->
                                        l.getCantidadActual()==null
                                                ? 0
                                                : l.getCantidadActual()
                        )
                        .toArray();

        for(int i=0;i<cantidadPresentaciones;i++){
            boolean encontrado=false;

            for(int j=0;j<restante.length;j++){
                if(restante[j]>=factor){
                    restante[j]-=factor;
                    encontrado=true;
                    break;
                }
            }

            if(!encontrado){
                throw new RuntimeException(
                        "No hay suficiente stock por lotes vigentes para vender "+
                                cantidadPresentaciones+" "+
                                (factor==1
                                        ? "unidad(es)"
                                        : "presentación(es)")+
                                " de "+producto.getNombreComercial()
                );
            }
        }
    }

    private void descontarPorFEFOYGuardarDetalles(
            Ventas venta,
            ItemCalculado item
    ){
        Productos producto=item.producto();
        int factor=item.precio().factor();

        List<Lotes> lotes=
                lotesRepository.buscarLotesFEFO(
                        producto.getIdProducto(),
                        LocalDate.now()
                );

        Map<Long,Integer> presentacionesPorLote=new LinkedHashMap<>();
        Map<Long,Lotes> lotePorId=new LinkedHashMap<>();

        for(int i=0;i<item.cantidad();i++){
            Lotes elegido=null;

            for(Lotes lote:lotes){
                int disponible=
                        lote.getCantidadActual()==null
                                ? 0
                                : lote.getCantidadActual();

                if(disponible>=factor){
                    elegido=lote;
                    break;
                }
            }

            if(elegido==null){
                throw new RuntimeException(
                        "Stock por lotes insuficiente para "+
                                producto.getNombreComercial()
                );
            }

            elegido.setCantidadActual(
                    elegido.getCantidadActual()-factor
            );

            presentacionesPorLote.merge(
                    elegido.getIdLote(),
                    1,
                    Integer::sum
            );

            lotePorId.put(
                    elegido.getIdLote(),
                    elegido
            );
        }

        for(Lotes lote:lotePorId.values()){
            lotesRepository.save(lote);
        }

        for(Map.Entry<Long,Integer> entry:presentacionesPorLote.entrySet()){
            int cantidadPresentaciones=entry.getValue();

            DetallesVentas detalle=new DetallesVentas();

            detalle.setVenta(venta);
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setIdFormula(item.idFormula());
            detalle.setIdLote(entry.getKey());
            detalle.setTipoVenta(item.tipoVenta());
            detalle.setCantidad(cantidadPresentaciones);
            detalle.setUnidadesDescontadas(cantidadPresentaciones*factor);
            detalle.setPrecioUnitario(item.precio().precio());
            detalle.setPorcentajeIva(producto.getPorcentajeIva()==null?BigDecimal.ZERO:producto.getPorcentajeIva());

            detalle.setSubtotal(
                    item.precio()
                            .precio()
                            .multiply(BigDecimal.valueOf(cantidadPresentaciones))
                            .setScale(2,RoundingMode.HALF_UP)
            );

            detallesVentasRepository.save(detalle);
        }

        Integer stockReal=lotesRepository.sumarStockDisponible(producto.getIdProducto(),LocalDate.now());
        producto.setStockTotal(stockReal==null?0:stockReal);
        productosRepository.save(producto);
    }

    private String generarNumeroFactura(){
        return "FARM-"+
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMddHHmmssSSS"
                                )
                        );
    }

    private record PrecioVenta(int factor,BigDecimal precio){}

    private record ItemCalculado(
            Productos producto,
            String tipoVenta,
            int cantidad,
            PrecioVenta precio,
            Long idFormula
    ){}

    public static class VentaRequest{
        private Long idSucursal;
        private Long idCliente;
        private Long idEmpleado;
        private BigDecimal descuento;
        private Boolean aplicaIva=true;
        private String metodoPago;
        private Boolean esDomicilio=false;
        private String direccionEntrega;
        private String telefonoContacto;
        private String nombreDomiciliario;
        private BigDecimal costoDomicilio=BigDecimal.ZERO;
        private List<ItemVentaRequest> items;

        public Long getIdSucursal(){return idSucursal;}
        public void setIdSucursal(Long idSucursal){this.idSucursal=idSucursal;}
        public Long getIdCliente(){return idCliente;}
        public void setIdCliente(Long idCliente){this.idCliente=idCliente;}
        public Long getIdEmpleado(){return idEmpleado;}
        public void setIdEmpleado(Long idEmpleado){this.idEmpleado=idEmpleado;}
        public BigDecimal getDescuento(){return descuento;}
        public void setDescuento(BigDecimal descuento){this.descuento=descuento;}
        public Boolean getAplicaIva(){return aplicaIva;}
        public void setAplicaIva(Boolean aplicaIva){this.aplicaIva=aplicaIva;}
        public String getMetodoPago(){return metodoPago;}
        public void setMetodoPago(String metodoPago){this.metodoPago=metodoPago;}
        public Boolean getEsDomicilio(){return esDomicilio;}
        public void setEsDomicilio(Boolean esDomicilio){this.esDomicilio=esDomicilio;}
        public String getDireccionEntrega(){return direccionEntrega;}
        public void setDireccionEntrega(String direccionEntrega){this.direccionEntrega=direccionEntrega;}
        public String getTelefonoContacto(){return telefonoContacto;}
        public void setTelefonoContacto(String telefonoContacto){this.telefonoContacto=telefonoContacto;}
        public String getNombreDomiciliario(){return nombreDomiciliario;}
        public void setNombreDomiciliario(String nombreDomiciliario){this.nombreDomiciliario=nombreDomiciliario;}
        public BigDecimal getCostoDomicilio(){return costoDomicilio;}
        public void setCostoDomicilio(BigDecimal costoDomicilio){this.costoDomicilio=costoDomicilio;}
        public List<ItemVentaRequest> getItems(){return items;}
        public void setItems(List<ItemVentaRequest> items){this.items=items;}
    }

    public static class ItemVentaRequest{
        private Long idProducto;
        private Long idFormula;
        private String tipoVenta="UNIDAD";
        private Integer cantidad=1;

        public Long getIdProducto(){return idProducto;}
        public void setIdProducto(Long idProducto){this.idProducto=idProducto;}
        public Long getIdFormula(){return idFormula;}
        public void setIdFormula(Long idFormula){this.idFormula=idFormula;}
        public String getTipoVenta(){return tipoVenta;}
        public void setTipoVenta(String tipoVenta){this.tipoVenta=tipoVenta;}
        public Integer getCantidad(){return cantidad;}
        public void setCantidad(Integer cantidad){this.cantidad=cantidad;}
    }
}
