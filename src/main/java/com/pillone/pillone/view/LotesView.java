package com.pillone.pillone.view;

import com.pillone.pillone.model.Lotes;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.LotesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class LotesView {

    private final LotesRepository lotesRepository;
    private final ProductosRepository productosRepository;
    private final JdbcTemplate jdbcTemplate;

    public LotesView(
            LotesRepository lotesRepository,
            ProductosRepository productosRepository,
            JdbcTemplate jdbcTemplate
    ){
        this.lotesRepository=lotesRepository;
        this.productosRepository=productosRepository;
        this.jdbcTemplate=jdbcTemplate;
    }

    @GetMapping("/view/lotes")
    public String listar(
            @RequestParam(required=false) Long productoId,
            @RequestParam(required=false) Long loteId,
            Model model
    ){
        actualizarEstadosYSincronizarStock();

        List<Productos> productos=productosRepository.findAll();

        Map<Long,Productos> productosMap=productos.stream()
                .collect(Collectors.toMap(
                        Productos::getIdProducto,
                        producto->producto
                ));

        List<Lotes> lotes=productoId==null
                ? lotesRepository.findAll()
                : lotesRepository.findByIdProductoOrderByFechaVencimientoAscIdLoteAsc(productoId);

        lotes.sort(
                Comparator.comparing(
                        Lotes::getFechaVencimiento,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).thenComparing(Lotes::getIdLote)
        );

        List<LoteFila> filas=lotes.stream()
                .map(lote->new LoteFila(
                        lote,
                        productosMap.get(lote.getIdProducto()),
                        puedeEliminarLote(lote)
                ))
                .toList();

        model.addAttribute("filas",filas);
        model.addAttribute("productos",productos);
        model.addAttribute("productoId",productoId);
        model.addAttribute("loteId",loteId);

        if(productoId!=null){
            model.addAttribute(
                    "productoSeleccionado",
                    productosMap.get(productoId)
            );
        }

        return "lotes/lotes";
    }

    @GetMapping("/view/lotes/form")
    public String formulario(
            @RequestParam(required=false) Long productoId,
            Model model
    ){
        model.addAttribute(
                "productos",
                productosRepository.findAll()
        );

        model.addAttribute(
                "productoId",
                productoId
        );

        if(productoId!=null){
            productosRepository.findById(productoId)
                    .ifPresent(producto->
                            model.addAttribute(
                                    "productoSeleccionado",
                                    producto
                            )
                    );
        }

        return "lotes/lotesForm";
    }

    @PostMapping("/view/lotes/save")
    public String guardar(
            @RequestParam Long idProducto,
            @RequestParam String numeroLote,
            @RequestParam(required=false)
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
            LocalDate fechaFabricacion,
            @RequestParam
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
            LocalDate fechaVencimiento,
            @RequestParam String tipoEntrada,
            @RequestParam Integer cantidadRecibida,
            Model model,
            RedirectAttributes ra
    ){
        Productos producto=productosRepository
                .findById(idProducto)
                .orElse(null);

        if(producto==null){
            return errorFormulario(
                    model,
                    idProducto,
                    "El producto seleccionado no existe."
            );
        }

        if(numeroLote==null || numeroLote.trim().isEmpty()){
            return errorFormulario(
                    model,
                    idProducto,
                    "Debe ingresar el número de lote."
            );
        }

        if(fechaVencimiento==null){
            return errorFormulario(
                    model,
                    idProducto,
                    "Debe ingresar la fecha de vencimiento."
            );
        }

        if(fechaFabricacion!=null &&
                fechaFabricacion.isAfter(fechaVencimiento)){
            return errorFormulario(
                    model,
                    idProducto,
                    "La fecha de fabricación no puede ser posterior al vencimiento."
            );
        }

        if(lotesRepository.existsByIdProductoAndNumeroLoteIgnoreCase(
                idProducto,
                numeroLote.trim()
        )){
            return errorFormulario(
                    model,
                    idProducto,
                    "Ese número de lote ya está registrado para este producto."
            );
        }

        if(cantidadRecibida==null || cantidadRecibida<=0){
            return errorFormulario(
                    model,
                    idProducto,
                    "La cantidad recibida debe ser mayor que cero."
            );
        }

        String tipo=tipoEntrada==null
                ? ""
                : tipoEntrada.trim().toUpperCase();

        if(!List.of("CAJA","SELLO","UNIDAD").contains(tipo)){
            return errorFormulario(
                    model,
                    idProducto,
                    "Tipo de entrada inválido."
            );
        }

        int factor=calcularFactor(
                producto,
                tipo
        );

        long total=(long)cantidadRecibida*factor;

        if(total>Integer.MAX_VALUE){
            return errorFormulario(
                    model,
                    idProducto,
                    "La cantidad total ingresada es demasiado grande."
            );
        }

        Lotes lote=new Lotes();

        lote.setIdProducto(idProducto);
        lote.setNumeroLote(numeroLote.trim());
        lote.setFechaFabricacion(fechaFabricacion);
        lote.setFechaVencimiento(fechaVencimiento);
        lote.setCantidadInicial((int)total);
        lote.setCantidadActual((int)total);
        lote.setEstado(
                calcularEstado(fechaVencimiento)
        );

        lotesRepository.save(lote);

        sincronizarStockProducto(
                idProducto
        );

        ra.addFlashAttribute(
                "mensaje",
                "Lote creado correctamente. Stock ingresado: "+
                        total+
                        " unidades."
        );

        return "redirect:/view/lotes?productoId="+
                idProducto+
                "&loteId="+
                lote.getIdLote();
    }

    @PostMapping("/view/lotes/{id}/retirar")
    public String retirar(
            @PathVariable Long id,
            @RequestParam(required=false) Long productoIdActual,
            RedirectAttributes ra
    ){
        Lotes lote=lotesRepository
                .findById(id)
                .orElse(null);

        if(lote==null){
            ra.addFlashAttribute(
                    "error",
                    "El lote no existe."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        if(lote.getFechaVencimiento()!=null &&
                lote.getFechaVencimiento().isBefore(LocalDate.now())){

            lote.setEstado("VENCIDO");

            lotesRepository.save(lote);

            sincronizarStockProducto(
                    lote.getIdProducto()
            );

            ra.addFlashAttribute(
                    "error",
                    "El lote ya está vencido. No es necesario retirarlo."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        lote.setEstado("RETIRADO");

        lotesRepository.save(lote);

        sincronizarStockProducto(
                lote.getIdProducto()
        );

        ra.addFlashAttribute(
                "mensaje",
                "Lote retirado correctamente. Ya no participará en ventas ni en el stock disponible."
        );

        return construirRedireccion(
                productoIdActual
        );
    }

    @PostMapping("/view/lotes/{id}/reactivar")
    public String reactivar(
            @PathVariable Long id,
            @RequestParam(required=false) Long productoIdActual,
            RedirectAttributes ra
    ){
        Lotes lote=lotesRepository
                .findById(id)
                .orElse(null);

        if(lote==null){
            ra.addFlashAttribute(
                    "error",
                    "El lote no existe."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        if(lote.getFechaVencimiento()==null){
            ra.addFlashAttribute(
                    "error",
                    "El lote no tiene fecha de vencimiento."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        if(lote.getFechaVencimiento().isBefore(LocalDate.now())){
            lote.setEstado("VENCIDO");

            lotesRepository.save(lote);

            sincronizarStockProducto(
                    lote.getIdProducto()
            );

            ra.addFlashAttribute(
                    "error",
                    "El lote está vencido y no puede reactivarse."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        lote.setEstado(
                calcularEstado(
                        lote.getFechaVencimiento()
                )
        );

        lotesRepository.save(lote);

        sincronizarStockProducto(
                lote.getIdProducto()
        );

        ra.addFlashAttribute(
                "mensaje",
                "Lote reactivado correctamente."
        );

        return construirRedireccion(
                productoIdActual
        );
    }

    @PostMapping("/view/lotes/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            @RequestParam(required=false) Long productoIdActual,
            RedirectAttributes ra
    ){
        Lotes lote=lotesRepository
                .findById(id)
                .orElse(null);

        if(lote==null){
            ra.addFlashAttribute(
                    "error",
                    "El lote que intenta eliminar no existe."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        Long idProducto=lote.getIdProducto();

        if(lote.getFechaVencimiento()==null ||
                !lote.getFechaVencimiento().isBefore(LocalDate.now())){

            ra.addFlashAttribute(
                    "error",
                    "Solo se pueden eliminar definitivamente lotes vencidos."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        int ventas=contarDetallesVenta(id);

        int movimientos=
                contarMovimientosInventario(id);

        if(ventas>0 || movimientos>0){

            lote.setEstado("VENCIDO");

            lotesRepository.save(lote);

            ra.addFlashAttribute(
                    "error",
                    "Este lote no se puede eliminar porque tiene historial asociado. "+
                            "Ventas relacionadas: "+ventas+
                            ". Movimientos de inventario: "+movimientos+
                            ". Se conservará como VENCIDO."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }

        try{

            lotesRepository.delete(lote);
            lotesRepository.flush();

            sincronizarStockProducto(
                    idProducto
            );

            ra.addFlashAttribute(
                    "mensaje",
                    "Lote vencido eliminado definitivamente."
            );

            return construirRedireccion(
                    productoIdActual
            );

        }catch(Exception e){

            ra.addFlashAttribute(
                    "error",
                    "No fue posible eliminar el lote porque existen registros relacionados con él."
            );

            return construirRedireccion(
                    productoIdActual
            );
        }
    }

    private String construirRedireccion(
            Long productoIdActual
    ){
        if(productoIdActual==null){
            return "redirect:/view/lotes";
        }

        return "redirect:/view/lotes?productoId="+
                productoIdActual;
    }

    private int contarDetallesVenta(
            Long idLote
    ){
        Integer total=jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM detalles_ventas WHERE id_lote = ?",
                Integer.class,
                idLote
        );

        return total==null
                ? 0
                : total;
    }

    private int contarMovimientosInventario(
            Long idLote
    ){
        Integer total=jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM movimientos_inventario WHERE id_lote = ?",
                Integer.class,
                idLote
        );

        return total==null
                ? 0
                : total;
    }

    private boolean puedeEliminarLote(
            Lotes lote
    ){
        if(lote==null ||
                lote.getFechaVencimiento()==null ||
                !lote.getFechaVencimiento().isBefore(LocalDate.now())){
            return false;
        }

        return contarDetallesVenta(lote.getIdLote())==0 &&
                contarMovimientosInventario(lote.getIdLote())==0;
    }

    private String errorFormulario(
            Model model,
            Long productoId,
            String mensaje
    ){
        model.addAttribute(
                "error",
                mensaje
        );

        model.addAttribute(
                "productos",
                productosRepository.findAll()
        );

        model.addAttribute(
                "productoId",
                productoId
        );

        if(productoId!=null){
            productosRepository.findById(productoId)
                    .ifPresent(producto->
                            model.addAttribute(
                                    "productoSeleccionado",
                                    producto
                            )
                    );
        }

        return "lotes/lotesForm";
    }

    private int calcularFactor(
            Productos producto,
            String tipo
    ){
        int unidadesPorSello=
                producto.getUnidadesPorSello()==null ||
                        producto.getUnidadesPorSello()<=0
                        ? 1
                        : producto.getUnidadesPorSello();

        int unidadesPorCaja=
                producto.getUnidadesPorEmpaque()==null ||
                        producto.getUnidadesPorEmpaque()<=0
                        ? 1
                        : producto.getUnidadesPorEmpaque();

        return switch(tipo){
            case "CAJA" -> unidadesPorCaja;
            case "SELLO" -> unidadesPorSello;
            default -> 1;
        };
    }

    private String calcularEstado(
            LocalDate vencimiento
    ){
        if(vencimiento==null){
            return "DISPONIBLE";
        }

        LocalDate hoy=
                LocalDate.now();

        if(vencimiento.isBefore(hoy)){
            return "VENCIDO";
        }

        long dias=
                ChronoUnit.DAYS.between(
                        hoy,
                        vencimiento
                );

        return dias<=90
                ? "PROXIMO_A_VENCER"
                : "DISPONIBLE";
    }

    private void actualizarEstadosYSincronizarStock(){
        List<Lotes> lotes=
                lotesRepository.findAll();

        Set<Long> productosAfectados=
                new HashSet<>();

        for(Lotes lote:lotes){

            productosAfectados.add(
                    lote.getIdProducto()
            );

            if("DEVUELTO".equalsIgnoreCase(
                    lote.getEstado()
            )){
                continue;
            }

            if("RETIRADO".equalsIgnoreCase(
                    lote.getEstado()
            )){

                if(lote.getFechaVencimiento()!=null &&
                        lote.getFechaVencimiento().isBefore(
                                LocalDate.now()
                        )){

                    lote.setEstado(
                            "VENCIDO"
                    );

                    lotesRepository.save(
                            lote
                    );
                }

                continue;
            }

            String nuevoEstado=
                    calcularEstado(
                            lote.getFechaVencimiento()
                    );

            if(lote.getEstado()==null ||
                    !nuevoEstado.equalsIgnoreCase(
                            lote.getEstado()
                    )){

                lote.setEstado(
                        nuevoEstado
                );

                lotesRepository.save(
                        lote
                );
            }
        }

        productosAfectados.forEach(
                this::sincronizarStockProducto
        );
    }

    private void sincronizarStockProducto(
            Long idProducto
    ){
        Productos producto=
                productosRepository
                        .findById(idProducto)
                        .orElse(null);

        if(producto==null){
            return;
        }

        Integer stock=
                lotesRepository
                        .sumarStockDisponible(
                                idProducto,
                                LocalDate.now()
                        );

        producto.setStockTotal(
                stock==null
                        ? 0
                        : stock
        );

        productosRepository.save(
                producto
        );
    }

    public static class LoteFila {

        private final Lotes lote;
        private final Productos producto;

        private final int sellosPorCaja;
        private final int unidadesPorSello;
        private final int unidadesPorCaja;

        private final int totalUnidades;
        private final int totalSellosCompletos;
        private final int totalCajasCompletas;

        private final int cajasDesglose;
        private final int sellosDesglose;
        private final int unidadesDesglose;

        private final boolean puedeEliminar;

        public LoteFila(
                Lotes lote,
                Productos producto,
                boolean puedeEliminar
        ){
            this.lote=lote;
            this.producto=producto;
            this.puedeEliminar=puedeEliminar;

            this.sellosPorCaja=
                    producto==null ||
                            producto.getSellosPorCaja()==null ||
                            producto.getSellosPorCaja()<=0
                            ? 1
                            : producto.getSellosPorCaja();

            this.unidadesPorSello=
                    producto==null ||
                            producto.getUnidadesPorSello()==null ||
                            producto.getUnidadesPorSello()<=0
                            ? 1
                            : producto.getUnidadesPorSello();

            int unidadesCajaCalculadas=
                    this.sellosPorCaja*
                            this.unidadesPorSello;

            this.unidadesPorCaja=
                    producto==null ||
                            producto.getUnidadesPorEmpaque()==null ||
                            producto.getUnidadesPorEmpaque()<=0
                            ? unidadesCajaCalculadas
                            : producto.getUnidadesPorEmpaque();

            this.totalUnidades=
                    lote.getCantidadActual()==null
                            ? 0
                            : Math.max(
                            0,
                            lote.getCantidadActual()
                    );

            this.totalCajasCompletas=
                    this.unidadesPorCaja>0
                            ? this.totalUnidades/
                            this.unidadesPorCaja
                            : 0;

            this.totalSellosCompletos=
                    this.unidadesPorSello>0
                            ? this.totalUnidades/
                            this.unidadesPorSello
                            : 0;

            this.cajasDesglose=
                    this.totalCajasCompletas;

            int restoCaja=
                    this.unidadesPorCaja>0
                            ? this.totalUnidades%
                            this.unidadesPorCaja
                            : this.totalUnidades;

            this.sellosDesglose=
                    this.unidadesPorSello>0
                            ? restoCaja/
                            this.unidadesPorSello
                            : 0;

            this.unidadesDesglose=
                    this.unidadesPorSello>0
                            ? restoCaja%
                            this.unidadesPorSello
                            : restoCaja;
        }

        public Lotes getLote(){
            return lote;
        }

        public Productos getProducto(){
            return producto;
        }

        public int getSellosPorCaja(){
            return sellosPorCaja;
        }

        public int getUnidadesPorSello(){
            return unidadesPorSello;
        }

        public int getUnidadesPorCaja(){
            return unidadesPorCaja;
        }

        public int getTotalUnidades(){
            return totalUnidades;
        }

        public int getTotalSellosCompletos(){
            return totalSellosCompletos;
        }

        public int getTotalCajasCompletas(){
            return totalCajasCompletas;
        }

        public int getCajasDesglose(){
            return cajasDesglose;
        }

        public int getSellosDesglose(){
            return sellosDesglose;
        }

        public int getUnidadesDesglose(){
            return unidadesDesglose;
        }

        public boolean isPuedeEliminar(){
            return puedeEliminar;
        }
    }
}