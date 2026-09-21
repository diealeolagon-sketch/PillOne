package com.pillone.pillone.controller;

import com.pillone.pillone.service.InventarioStockService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;
    private final InventarioStockService inventarioStockService;

    public DashboardController(
            JdbcTemplate jdbcTemplate,
            InventarioStockService inventarioStockService
    ){
        this.jdbcTemplate=jdbcTemplate;
        this.inventarioStockService=inventarioStockService;
    }

    @GetMapping
    public Map<String,Object> dashboard(){

        inventarioStockService.sincronizarTodo();

        Map<String,Object> respuesta=new LinkedHashMap<>();

        respuesta.put("resumen", obtenerResumen());
        respuesta.put("ventasSemana", obtenerVentasSemana());
        respuesta.put("domicilios", obtenerDomicilios());
        respuesta.put("alertas", obtenerAlertas());
        respuesta.put("productosMasVendidos", obtenerProductosMasVendidos());
        respuesta.put("ultimasVentas", obtenerUltimasVentas());
        respuesta.put("metodosPago", obtenerMetodosPago());

        return respuesta;
    }

    private Map<String,Object> obtenerResumen(){

        Map<String,Object> resumen=new LinkedHashMap<>();

        BigDecimal ventasBrutasHoy=valorDecimal("""
            SELECT COALESCE(SUM(total),0)
            FROM ventas
            WHERE DATE(fecha_venta)=CURDATE()
              AND estado='PAGADA'
        """);

        BigDecimal devolucionesHoy=valorDecimal("""
            SELECT COALESCE(SUM(
                LEAST(dev.cantidad_devuelta, det.cantidad_vendida)
                * (det.total_producto / NULLIF(det.cantidad_vendida,0))
            ),0)
            FROM (
                SELECT id_venta,id_producto,SUM(cantidad) AS cantidad_devuelta
                FROM devoluciones
                WHERE tipo_devolucion='CLIENTE'
                  AND DATE(fecha_devolucion)=CURDATE()
                GROUP BY id_venta,id_producto
            ) dev
            INNER JOIN (
                SELECT id_venta,id_producto,
                       SUM(cantidad) AS cantidad_vendida,
                       SUM(subtotal) AS total_producto
                FROM detalles_ventas
                GROUP BY id_venta,id_producto
            ) det
                ON det.id_venta=dev.id_venta
               AND det.id_producto=dev.id_producto
            INNER JOIN ventas v
                ON v.id_venta=dev.id_venta
            WHERE v.estado='PAGADA'
        """);

        BigDecimal ventasHoy=ventasBrutasHoy.subtract(devolucionesHoy);
        if(ventasHoy.signum()<0){
            ventasHoy=BigDecimal.ZERO;
        }

        Integer cantidadVentasHoy=valorEntero("""
            SELECT COUNT(*)
            FROM ventas
            WHERE DATE(fecha_venta)=CURDATE()
              AND estado='PAGADA'
        """);

        BigDecimal ticketPromedio=BigDecimal.ZERO;

        if(cantidadVentasHoy!=null && cantidadVentasHoy>0){
            ticketPromedio=ventasHoy.divide(
                    BigDecimal.valueOf(cantidadVentasHoy),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
        }

        Integer productosStockBajo=valorEntero("""
            SELECT COUNT(*)
            FROM productos
            WHERE estado='ACTIVO'
              AND COALESCE(stock_total,0)>0
              AND COALESCE(stock_total,0)<=COALESCE(stock_minimo,0)
        """);

        Integer productosSinStock=valorEntero("""
            SELECT COUNT(*)
            FROM productos
            WHERE estado='ACTIVO'
              AND COALESCE(stock_total,0)<=0
        """);

        Integer lotesCriticos=valorEntero("""
            SELECT COUNT(*)
            FROM lotes
            WHERE cantidad_actual>0
              AND fecha_vencimiento>=CURDATE()
              AND fecha_vencimiento<=DATE_ADD(CURDATE(),INTERVAL 30 DAY)
              AND estado NOT IN ('RETIRADO','DEVUELTO')
        """);

        Integer lotesVencidos=valorEntero("""
            SELECT COUNT(*)
            FROM lotes
            WHERE cantidad_actual>0
              AND fecha_vencimiento<CURDATE()
              AND estado NOT IN ('RETIRADO','DEVUELTO')
        """);

        Integer domiciliosPendientes=valorEntero("""
            SELECT COUNT(*)
            FROM domicilios
            WHERE estado IN ('PENDIENTE','EN_PREPARACION','EN_CAMINO')
        """);

        Integer totalProductos=valorEntero("""
            SELECT COUNT(*)
            FROM productos
            WHERE estado='ACTIVO'
        """);

        resumen.put("ventasHoy",ventasHoy);
        resumen.put("devolucionesHoy",devolucionesHoy);
        resumen.put("cantidadVentasHoy",cantidadVentasHoy);
        resumen.put("ticketPromedio",ticketPromedio);
        resumen.put("productosStockBajo",productosStockBajo);
        resumen.put("productosSinStock",productosSinStock);
        resumen.put("lotesCriticos",lotesCriticos);
        resumen.put("lotesVencidos",lotesVencidos);
        resumen.put("domiciliosPendientes",domiciliosPendientes);
        resumen.put("totalProductos",totalProductos);

        return resumen;
    }

    private List<Map<String,Object>> obtenerVentasSemana(){

        LocalDate hoy=LocalDate.now();
        LocalDate inicio=hoy.minusDays(6);

        List<Map<String,Object>> consulta=jdbcTemplate.queryForList("""
            SELECT
                dias.fecha,
                GREATEST(
                    COALESCE(vtas.total,0)-COALESCE(devs.total_devuelto,0),
                    0
                ) AS total,
                COALESCE(vtas.cantidad,0) AS cantidad
            FROM (
                SELECT DATE(fecha_venta) AS fecha
                FROM ventas
                WHERE DATE(fecha_venta) BETWEEN ? AND ?
                UNION
                SELECT DATE(fecha_devolucion) AS fecha
                FROM devoluciones
                WHERE tipo_devolucion='CLIENTE'
                  AND DATE(fecha_devolucion) BETWEEN ? AND ?
            ) dias
            LEFT JOIN (
                SELECT DATE(fecha_venta) AS fecha,
                       SUM(total) AS total,
                       COUNT(*) AS cantidad
                FROM ventas
                WHERE DATE(fecha_venta) BETWEEN ? AND ?
                  AND estado='PAGADA'
                GROUP BY DATE(fecha_venta)
            ) vtas ON vtas.fecha=dias.fecha
            LEFT JOIN (
                SELECT DATE(d.fecha_devolucion) AS fecha,
                       SUM(
                           LEAST(d.cantidad_devuelta,det.cantidad_vendida)
                           * (det.total_producto/NULLIF(det.cantidad_vendida,0))
                       ) AS total_devuelto
                FROM (
                    SELECT DATE(fecha_devolucion) AS fecha_devolucion,
                           id_venta,id_producto,SUM(cantidad) AS cantidad_devuelta
                    FROM devoluciones
                    WHERE tipo_devolucion='CLIENTE'
                      AND DATE(fecha_devolucion) BETWEEN ? AND ?
                    GROUP BY DATE(fecha_devolucion),id_venta,id_producto
                ) d
                INNER JOIN (
                    SELECT id_venta,id_producto,
                           SUM(cantidad) AS cantidad_vendida,
                           SUM(subtotal) AS total_producto
                    FROM detalles_ventas
                    GROUP BY id_venta,id_producto
                ) det ON det.id_venta=d.id_venta
                     AND det.id_producto=d.id_producto
                INNER JOIN ventas v ON v.id_venta=d.id_venta
                WHERE v.estado='PAGADA'
                GROUP BY DATE(d.fecha_devolucion)
            ) devs ON devs.fecha=dias.fecha
            ORDER BY dias.fecha
        """,inicio,hoy,inicio,hoy,inicio,hoy,inicio,hoy);

        Map<LocalDate,Map<String,Object>> porFecha=new HashMap<>();

        for(Map<String,Object> fila:consulta){

            Object fechaObj=fila.get("fecha");

            LocalDate fecha;

            if(fechaObj instanceof java.sql.Date sqlDate){
                fecha=sqlDate.toLocalDate();
            }else{
                fecha=LocalDate.parse(fechaObj.toString());
            }

            porFecha.put(fecha,fila);
        }

        List<Map<String,Object>> resultado=new ArrayList<>();

        for(int i=0;i<7;i++){

            LocalDate fecha=inicio.plusDays(i);

            Map<String,Object> filaBD=porFecha.get(fecha);

            BigDecimal total=BigDecimal.ZERO;
            int cantidad=0;

            if(filaBD!=null){

                Object totalObj=filaBD.get("total");

                if(totalObj instanceof BigDecimal){
                    total=(BigDecimal)totalObj;
                }else if(totalObj!=null){
                    total=new BigDecimal(totalObj.toString());
                }

                Object cantidadObj=filaBD.get("cantidad");

                if(cantidadObj instanceof Number){
                    cantidad=((Number)cantidadObj).intValue();
                }
            }

            Map<String,Object> dia=new LinkedHashMap<>();

            String nombre=fecha.getDayOfWeek()
                    .getDisplayName(
                            TextStyle.SHORT,
                            new Locale("es","CO")
                    );

            if(!nombre.isEmpty()){
                nombre=nombre.substring(0,1).toUpperCase()+nombre.substring(1);
            }

            dia.put("fecha",fecha.toString());
            dia.put("dia",nombre);
            dia.put("total",total);
            dia.put("cantidad",cantidad);

            resultado.add(dia);
        }

        return resultado;
    }

    private Map<String,Object> obtenerDomicilios(){

        Map<String,Object> datos=new LinkedHashMap<>();

        datos.put(
                "pendientes",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM domicilios
                    WHERE estado='PENDIENTE'
                """)
        );

        datos.put(
                "preparacion",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM domicilios
                    WHERE estado='EN_PREPARACION'
                """)
        );

        datos.put(
                "camino",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM domicilios
                    WHERE estado='EN_CAMINO'
                """)
        );

        datos.put(
                "entregadosHoy",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM domicilios
                    WHERE estado='ENTREGADO'
                      AND DATE(fecha_hora_entrega)=CURDATE()
                """)
        );

        return datos;
    }

    private Map<String,Object> obtenerAlertas(){

        Map<String,Object> alertas=new LinkedHashMap<>();

        alertas.put(
                "vencidos",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM lotes
                    WHERE cantidad_actual>0
                      AND fecha_vencimiento<CURDATE()
                      AND estado NOT IN ('RETIRADO','DEVUELTO')
                """)
        );

        alertas.put(
                "criticos",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM lotes
                    WHERE cantidad_actual>0
                      AND fecha_vencimiento>=CURDATE()
                      AND fecha_vencimiento<=DATE_ADD(CURDATE(),INTERVAL 30 DAY)
                      AND estado NOT IN ('RETIRADO','DEVUELTO')
                """)
        );

        alertas.put(
                "proximos",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM lotes
                    WHERE cantidad_actual>0
                      AND fecha_vencimiento>DATE_ADD(CURDATE(),INTERVAL 30 DAY)
                      AND fecha_vencimiento<=DATE_ADD(CURDATE(),INTERVAL 90 DAY)
                      AND estado NOT IN ('RETIRADO','DEVUELTO')
                """)
        );

        alertas.put(
                "stockBajo",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM productos
                    WHERE estado='ACTIVO'
                      AND COALESCE(stock_total,0)>0
                      AND COALESCE(stock_total,0)<=COALESCE(stock_minimo,0)
                """)
        );

        alertas.put(
                "sinStock",
                valorEntero("""
                    SELECT COUNT(*)
                    FROM productos
                    WHERE estado='ACTIVO'
                      AND COALESCE(stock_total,0)<=0
                """)
        );

        return alertas;
    }

    private List<Map<String,Object>> obtenerProductosMasVendidos(){

        return jdbcTemplate.queryForList("""
            SELECT
                p.id_producto AS idProducto,
                p.nombre_comercial AS producto,
                p.codigo_interno AS codigo,
                COALESCE(SUM(dv.cantidad),0) AS cantidadVendida,
                COALESCE(SUM(dv.subtotal),0) AS totalVendido
            FROM detalles_ventas dv
            INNER JOIN ventas v
                ON v.id_venta=dv.id_venta
            INNER JOIN productos p
                ON p.id_producto=dv.id_producto
            WHERE v.estado='PAGADA'
              AND v.fecha_venta>=DATE_SUB(NOW(),INTERVAL 30 DAY)
            GROUP BY
                p.id_producto,
                p.nombre_comercial,
                p.codigo_interno
            ORDER BY cantidadVendida DESC,totalVendido DESC
            LIMIT 5
        """);
    }

    private List<Map<String,Object>> obtenerUltimasVentas(){

        List<Map<String,Object>> datos=jdbcTemplate.queryForList("""
            SELECT
                v.id_venta AS idVenta,
                v.numero_factura AS factura,
                COALESCE(c.nombre_completo,'Cliente ocasional') AS cliente,
                v.fecha_venta AS fecha,
                v.metodo_pago AS metodoPago,
                v.total AS total,
                v.estado AS estado,
                CASE
                    WHEN d.id_domicilio IS NULL THEN 'MOSTRADOR'
                    ELSE 'DOMICILIO'
                END AS tipoVenta
            FROM ventas v
            LEFT JOIN clientes c
                ON c.id_cliente=v.id_cliente
            LEFT JOIN domicilios d
                ON d.id_venta=v.id_venta
            ORDER BY v.fecha_venta DESC
            LIMIT 6
        """);

        List<Map<String,Object>> resultado=new ArrayList<>();

        for(Map<String,Object> fila:datos){

            Map<String,Object> venta=new LinkedHashMap<>();

            venta.put("idVenta",fila.get("idVenta"));
            venta.put("factura",fila.get("factura"));
            venta.put("cliente",fila.get("cliente"));
            venta.put("metodoPago",fila.get("metodoPago"));
            venta.put("total",fila.get("total"));
            venta.put("estado",fila.get("estado"));
            venta.put("tipoVenta",fila.get("tipoVenta"));

            Object fechaObj=fila.get("fecha");

            if(fechaObj instanceof Timestamp timestamp){

                LocalDateTime fecha=timestamp.toLocalDateTime();

                venta.put(
                        "fecha",
                        fecha.toString()
                );

            }else{

                venta.put(
                        "fecha",
                        fechaObj!=null
                                ? fechaObj.toString()
                                : null
                );
            }

            resultado.add(venta);
        }

        return resultado;
    }

    private List<Map<String,Object>> obtenerMetodosPago(){

        return jdbcTemplate.queryForList("""
            SELECT
                v.metodo_pago AS metodo,
                COUNT(DISTINCT v.id_venta) AS cantidad,
                GREATEST(
                    COALESCE(SUM(v.total),0)-COALESCE(SUM(dev.total_devuelto),0),
                    0
                ) AS total
            FROM ventas v
            LEFT JOIN (
                SELECT d.id_venta,
                       SUM(
                           LEAST(d.cantidad_devuelta,det.cantidad_vendida)
                           * (det.total_producto/NULLIF(det.cantidad_vendida,0))
                       ) AS total_devuelto
                FROM (
                    SELECT id_venta,id_producto,SUM(cantidad) AS cantidad_devuelta
                    FROM devoluciones
                    WHERE tipo_devolucion='CLIENTE'
                    GROUP BY id_venta,id_producto
                ) d
                INNER JOIN (
                    SELECT id_venta,id_producto,
                           SUM(cantidad) AS cantidad_vendida,
                           SUM(subtotal) AS total_producto
                    FROM detalles_ventas
                    GROUP BY id_venta,id_producto
                ) det ON det.id_venta=d.id_venta
                     AND det.id_producto=d.id_producto
                GROUP BY d.id_venta
            ) dev ON dev.id_venta=v.id_venta
            WHERE v.estado='PAGADA'
              AND DATE(v.fecha_venta)>=DATE_SUB(CURDATE(),INTERVAL 30 DAY)
            GROUP BY v.metodo_pago
            ORDER BY total DESC
        """);
    }

    private Integer valorEntero(String sql){

        Integer valor=jdbcTemplate.queryForObject(
                sql,
                Integer.class
        );

        return valor==null ? 0 : valor;
    }

    private BigDecimal valorDecimal(String sql){

        BigDecimal valor=jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class
        );

        return valor==null
                ? BigDecimal.ZERO
                : valor;
    }
}