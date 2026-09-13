package com.pillone.pillone.view;

import com.pillone.pillone.model.Devoluciones;
import com.pillone.pillone.repository.DevolucionesRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/view/devoluciones")
public class DevolucionesView {
    private static final Set<String> MOTIVOS=Set.of(
            "PRODUCTO_DEFECTUOSO",
            "ERROR_ENTREGA",
            "PROXIMO_A_VENCER",
            "VENCIDO",
            "RETIRO_MERCADO",
            "EMPAQUE_DANADO"
    );

    private final DevolucionesRepository repo;
    private final JdbcTemplate jdbc;

    public DevolucionesView(DevolucionesRepository repo,JdbcTemplate jdbc){
        this.repo=repo;
        this.jdbc=jdbc;
    }

    @GetMapping
    public String lista(Model model){
        String sql="""
            SELECT d.id_devolucion,d.tipo_devolucion,d.id_venta,d.id_compra,d.id_producto,d.id_lote,
                   d.cantidad,d.motivo,d.estado_producto,d.fecha_devolucion,d.observaciones,
                   p.nombre_comercial,p.codigo_interno,l.numero_lote,
                   s.nombre AS sucursal,u.username,
                   v.numero_factura,c.numero_factura_proveedor,
                   cli.nombre_completo AS cliente,
                   dom.id_domicilio
            FROM devoluciones d
            JOIN productos p ON p.id_producto=d.id_producto
            JOIN sucursales s ON s.id_sucursal=d.id_sucursal
            JOIN usuarios u ON u.id_usuario=d.id_usuario_regente
            LEFT JOIN lotes l ON l.id_lote=d.id_lote
            LEFT JOIN ventas v ON v.id_venta=d.id_venta
            LEFT JOIN clientes cli ON cli.id_cliente=v.id_cliente
            LEFT JOIN domicilios dom ON dom.id_venta=d.id_venta
            LEFT JOIN compras c ON c.id_compra=d.id_compra
            ORDER BY d.fecha_devolucion DESC,d.id_devolucion DESC
            """;

        model.addAttribute("devoluciones",jdbc.queryForList(sql));
        return "devoluciones/devoluciones";
    }

    @PostMapping("/domicilio/{idDomicilio}")
    @Transactional
    public String devolverDesdeDomicilio(
            @PathVariable Long idDomicilio,
            @RequestParam Long idDetalleVenta,
            @RequestParam Integer cantidad,
            @RequestParam String motivo,
            @RequestParam String estadoProducto,
            @RequestParam(required=false) String observaciones,
            HttpSession session,
            RedirectAttributes ra
    ){
        try{
            Long idUsuario=numeroSesion(session.getAttribute("idUsuario"));

            if(idUsuario==null){
                throw new IllegalArgumentException("La sesión no tiene un usuario válido.");
            }

            String motivoN=normalizar(motivo);
            String estadoN=normalizar(estadoProducto);

            if(!MOTIVOS.contains(motivoN)){
                throw new IllegalArgumentException("Motivo de devolución no válido.");
            }

            if(!Set.of("APTO_PARA_REINGRESO","DESECHADO").contains(estadoN)){
                throw new IllegalArgumentException("Selecciona si el producto reingresa al inventario o debe desecharse.");
            }

            if(cantidad==null||cantidad<=0){
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
            }

            Map<String,Object> domicilio=unico("""
                SELECT d.id_domicilio,d.id_venta,d.estado,v.id_sucursal
                FROM domicilios d
                JOIN ventas v ON v.id_venta=d.id_venta
                WHERE d.id_domicilio=?
                """,idDomicilio);

            if(domicilio==null){
                throw new IllegalArgumentException("El domicilio no existe.");
            }

            if(!"EN_CAMINO".equalsIgnoreCase(String.valueOf(domicilio.get("estado")))){
                throw new IllegalArgumentException("La devolución solo puede registrarse mientras el domicilio está EN CAMINO.");
            }

            Long idVenta=numero(domicilio.get("id_venta"));
            Long idSucursal=numero(domicilio.get("id_sucursal"));

            Map<String,Object> detalle=unico("""
                SELECT dv.id_detalle_venta,dv.id_venta,dv.id_producto,dv.id_lote,
                       dv.unidades_descontadas,p.nombre_comercial,l.numero_lote
                FROM detalles_ventas dv
                JOIN productos p ON p.id_producto=dv.id_producto
                JOIN lotes l ON l.id_lote=dv.id_lote
                WHERE dv.id_detalle_venta=? AND dv.id_venta=?
                """,idDetalleVenta,idVenta);

            if(detalle==null){
                throw new IllegalArgumentException("El producto seleccionado no pertenece a este domicilio.");
            }

            Long idProducto=numero(detalle.get("id_producto"));
            Long idLote=numero(detalle.get("id_lote"));
            int vendido=((Number)detalle.get("unidades_descontadas")).intValue();

            Integer yaDevuelto=jdbc.queryForObject("""
                SELECT COALESCE(SUM(cantidad),0)
                FROM devoluciones
                WHERE tipo_devolucion='CLIENTE'
                  AND id_venta=?
                  AND id_producto=?
                  AND id_lote=?
                """,Integer.class,idVenta,idProducto,idLote);

            int disponible=Math.max(0,vendido-(yaDevuelto==null?0:yaDevuelto));

            if(disponible<=0){
                throw new IllegalArgumentException("Ese producto ya fue devuelto completamente.");
            }

            if(cantidad>disponible){
                throw new IllegalArgumentException("Solo puedes devolver hasta "+disponible+" unidad(es) de este producto.");
            }

            Devoluciones d=new Devoluciones();
            d.setIdSucursal(idSucursal);
            d.setTipoDevolucion("CLIENTE");
            d.setIdVenta(idVenta);
            d.setIdCompra(null);
            d.setIdProducto(idProducto);
            d.setIdLote(idLote);
            d.setCantidad(cantidad);
            d.setMotivo(motivoN);
            d.setEstadoProducto(estadoN);
            d.setIdUsuarioRegente(idUsuario);
            d.setObservaciones(limpiar(observaciones));
            repo.saveAndFlush(d);

            if("APTO_PARA_REINGRESO".equals(estadoN)){
                sumarStock(
                        idSucursal,
                        idProducto,
                        idLote,
                        cantidad,
                        idUsuario,
                        "Reingreso por devolución de domicilio #"+idDomicilio+" / devolución #"+d.getIdDevolucion()
                );
            }

            int pendientes=calcularUnidadesPendientes(idVenta);

            if(pendientes<=0){
                jdbc.update("""
                    UPDATE domicilios
                    SET estado='CANCELADO',
                        observaciones_entrega=CONCAT(
                            COALESCE(observaciones_entrega,''),
                            CASE WHEN COALESCE(observaciones_entrega,'')='' THEN '' ELSE ' | ' END,
                            'Domicilio devuelto completamente'
                        )
                    WHERE id_domicilio=?
                    """,idDomicilio);

                ra.addFlashAttribute("mensaje","Devolución registrada. El domicilio quedó cerrado como CANCELADO porque ya no quedan unidades pendientes.");
            }else{
                ra.addFlashAttribute("mensaje","Devolución registrada. El domicilio continúa EN CAMINO con las unidades restantes.");
            }

            return "redirect:/view/domicilios";

        }catch(Exception e){
            ra.addFlashAttribute("error",e.getMessage()==null?"No fue posible registrar la devolución.":e.getMessage());
            return "redirect:/view/domicilios";
        }
    }

    private int calcularUnidadesPendientes(Long idVenta){
        Integer pendientes=jdbc.queryForObject("""
            SELECT COALESCE(SUM(
                GREATEST(
                    dv.unidades_descontadas-COALESCE((
                        SELECT SUM(dev.cantidad)
                        FROM devoluciones dev
                        WHERE dev.tipo_devolucion='CLIENTE'
                          AND dev.id_venta=dv.id_venta
                          AND dev.id_producto=dv.id_producto
                          AND dev.id_lote=dv.id_lote
                    ),0),0
                )
            ),0)
            FROM detalles_ventas dv
            WHERE dv.id_venta=?
            """,Integer.class,idVenta);

        return pendientes==null?0:pendientes;
    }

    private void sumarStock(Long idSucursal,Long idProducto,Long idLote,int cantidad,Long idUsuario,String motivo){
        Integer anterior=jdbc.queryForObject(
                "SELECT cantidad_actual FROM lotes WHERE id_lote=? FOR UPDATE",
                Integer.class,
                idLote
        );

        if(anterior==null){
            throw new IllegalArgumentException("No fue posible leer el stock del lote.");
        }

        int nuevo=anterior+cantidad;

        jdbc.update("""
            UPDATE lotes
            SET cantidad_actual=?,
                estado=CASE
                    WHEN estado IN ('VENCIDO','RETIRADO','DEVUELTO') THEN estado
                    ELSE 'DISPONIBLE'
                END
            WHERE id_lote=?
            """,nuevo,idLote);

        jdbc.update("""
            UPDATE productos
            SET stock_total=COALESCE(stock_total,0)+?
            WHERE id_producto=?
            """,cantidad,idProducto);

        jdbc.update("""
            INSERT INTO movimientos_inventario
            (id_sucursal,tipo_movimiento,id_producto,id_lote,cantidad,existencia_anterior,nueva_existencia,id_usuario,motivo)
            VALUES (?,'DEVOLUCION_CLIENTE',?,?,?,?,?,?,?)
            """,idSucursal,idProducto,idLote,cantidad,anterior,nuevo,idUsuario,motivo);
    }

    private Map<String,Object> unico(String sql,Object... args){
        List<Map<String,Object>> r=jdbc.queryForList(sql,args);
        return r.isEmpty()?null:r.get(0);
    }

    private Long numero(Object valor){
        return valor instanceof Number?((Number)valor).longValue():null;
    }

    private Long numeroSesion(Object valor){
        return numero(valor);
    }

    private String normalizar(String valor){
        return valor==null?"":valor.trim().toUpperCase();
    }

    private String limpiar(String valor){
        if(valor==null) return null;
        String v=valor.trim();
        return v.isEmpty()?null:v;
    }
}
