package com.pillone.pillone.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionesController {
    private final JdbcTemplate jdbcTemplate;
    public NotificacionesController(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @GetMapping
    public Map<String,Object> obtenerNotificaciones() {
        Map<String,Object> r=new LinkedHashMap<>();
        List<Map<String,Object>> items=new ArrayList<>();

        jdbcTemplate.queryForList("""
            SELECT l.id_lote,l.numero_lote,l.fecha_vencimiento,l.cantidad_actual,
                   p.id_producto,p.nombre_comercial,DATEDIFF(l.fecha_vencimiento,CURDATE()) dias_restantes
            FROM lotes l JOIN productos p ON p.id_producto=l.id_producto
            WHERE l.cantidad_actual>0 AND l.fecha_vencimiento<=DATE_ADD(CURDATE(),INTERVAL 30 DAY)
              AND l.estado IN ('DISPONIBLE','PROXIMO_A_VENCER','VENCIDO')
            ORDER BY l.fecha_vencimiento ASC LIMIT 12
        """).forEach(x->items.add(item("LOTE-"+x.get("id_lote"),"INVENTARIO","Lotes",
                ((Number)x.get("dias_restantes")).intValue()<0?"CRITICA":"ALTA",
                ((Number)x.get("dias_restantes")).intValue()<0?"Lote vencido":"Lote próximo a vencer",
                x.get("nombre_comercial")+" · Lote "+x.get("numero_lote")+" · "+x.get("fecha_vencimiento"),
                "/view/lotes")));

        jdbcTemplate.queryForList("""
            SELECT p.id_producto,p.nombre_comercial,COALESCE(p.stock_total,0) stock_total,COALESCE(p.stock_minimo,0) stock_minimo
            FROM productos p WHERE p.estado='ACTIVO'
              AND COALESCE(p.stock_total,0)<=COALESCE(p.stock_minimo,0)
            ORDER BY p.stock_total ASC LIMIT 12
        """).forEach(x->items.add(item("STOCK-"+x.get("id_producto"),"INVENTARIO","Productos","ALTA","Stock crítico",
                x.get("nombre_comercial")+" · Stock: "+x.get("stock_total")+" / mínimo: "+x.get("stock_minimo"),
                "/view/productos")));

        jdbcTemplate.queryForList("""
            SELECT v.id_venta,v.numero_factura,v.total,COALESCE(c.nombre_completo,'Cliente ocasional') cliente
            FROM ventas v LEFT JOIN clientes c ON c.id_cliente=v.id_cliente
            WHERE v.estado='DEUDA' ORDER BY v.fecha_venta DESC LIMIT 12
        """).forEach(x->items.add(item("DEUDA-"+x.get("id_venta"),"VENTAS","Ventas","MEDIA","Venta pendiente de pago",
                x.get("numero_factura")+" · "+x.get("cliente")+" · $"+x.get("total"),
                "/view/ventas/detalle/"+x.get("id_venta"))));

        jdbcTemplate.queryForList("""
            SELECT c.id_compra,c.numero_factura_proveedor,c.fecha_compra,p.razon_social proveedor
            FROM compras c JOIN proveedores p ON p.id_proveedor=c.id_proveedor
            WHERE c.estado='PENDIENTE' AND p.estado='ACTIVO' ORDER BY c.fecha_compra ASC LIMIT 12
        """).forEach(x->items.add(item("COMPRA-"+x.get("id_compra"),"COMPRAS","Compras","MEDIA","Compra pendiente por recibir",
                x.get("proveedor")+" · "+Objects.toString(x.get("numero_factura_proveedor"),"Sin factura"),
                "/view/compras")));

        jdbcTemplate.queryForList("""
            SELECT p.id_producto,p.nombre_comercial,
              CONCAT_WS(' · ',
                CASE WHEN p.codigo_barras IS NULL OR TRIM(p.codigo_barras)='' THEN 'sin código de barras' END,
                CASE WHEN p.registro_invima IS NULL OR TRIM(p.registro_invima)='' THEN 'sin INVIMA' END) motivo
            FROM productos p WHERE p.estado='ACTIVO' AND
              (p.codigo_barras IS NULL OR TRIM(p.codigo_barras)='' OR p.registro_invima IS NULL OR TRIM(p.registro_invima)='')
            LIMIT 12
        """).forEach(x->items.add(item("DATOS-"+x.get("id_producto"),"PRODUCTOS","Productos","BAJA","Producto con datos pendientes",
                x.get("nombre_comercial")+" · "+x.get("motivo"),
                "/view/productos/edit/"+x.get("id_producto"))));

        r.put("totalAlertas",items.size());
        r.put("notificaciones",items);
        r.put("generadoEn",System.currentTimeMillis());
        return r;
    }

    private Map<String,Object> item(String clave,String categoria,String origen,String prioridad,String titulo,String mensaje,String url){
        Map<String,Object> x=new LinkedHashMap<>();
        x.put("clave",clave); x.put("categoria",categoria); x.put("origen",origen); x.put("prioridad",prioridad); x.put("titulo",titulo); x.put("mensaje",mensaje); x.put("url",url);
        return x;
    }
}
