package com.pillone.pillone.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private final JdbcTemplate jdbcTemplate;

    public ReportesController(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @GetMapping
    public Map<String,Object> generarReporte(
            @RequestParam(required=false)
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam(required=false)
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ){

        if(hasta==null){
            hasta=LocalDate.now();
        }

        if(desde==null){
            desde=hasta.withDayOfMonth(1);
        }

        if(desde.isAfter(hasta)){
            LocalDate temporal=desde;
            desde=hasta;
            hasta=temporal;
        }

        Map<String,Object> respuesta=new LinkedHashMap<>();

        respuesta.put("desde",desde.toString());
        respuesta.put("hasta",hasta.toString());

        respuesta.put(
                "resumen",
                obtenerResumen(desde,hasta)
        );

        respuesta.put(
                "ventasPorDia",
                obtenerVentasPorDia(desde,hasta)
        );

        respuesta.put(
                "metodosPago",
                obtenerMetodosPago(desde,hasta)
        );

        respuesta.put(
                "productosMasVendidos",
                obtenerProductosMasVendidos(desde,hasta)
        );

        respuesta.put(
                "domicilios",
                obtenerDomicilios(desde,hasta)
        );

        respuesta.put(
                "inventario",
                obtenerInventarioActual()
        );

        respuesta.put(
                "detalleVentas",
                obtenerDetalleVentas(desde,hasta)
        );

        return respuesta;
    }

    private Map<String,Object> obtenerResumen(
            LocalDate desde,
            LocalDate hasta
    ){

        Map<String,Object> resumen=
                new LinkedHashMap<>();

        Map<String,Object> fila=
                jdbcTemplate.queryForMap("""
                    SELECT
                        COUNT(*) AS cantidadVentas,
                        COALESCE(SUM(subtotal),0) AS subtotal,
                        COALESCE(SUM(descuento),0) AS descuento,
                        COALESCE(SUM(impuesto_iva),0) AS iva,
                        COALESCE(SUM(total),0) AS total
                    FROM ventas
                    WHERE DATE(fecha_venta) BETWEEN ? AND ?
                      AND estado='PAGADA'
                """,desde,hasta);

        int cantidadVentas=
                numeroEntero(
                        fila.get("cantidadVentas")
                );

        BigDecimal subtotal=
                numeroDecimal(
                        fila.get("subtotal")
                );

        BigDecimal descuento=
                numeroDecimal(
                        fila.get("descuento")
                );

        BigDecimal iva=
                numeroDecimal(
                        fila.get("iva")
                );

        BigDecimal total=
                numeroDecimal(
                        fila.get("total")
                );

        BigDecimal ticketPromedio=
                BigDecimal.ZERO;

        if(cantidadVentas>0){

            ticketPromedio=
                    total.divide(
                            BigDecimal.valueOf(
                                    cantidadVentas
                            ),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        Integer ventasDomicilio=
                consultarEntero("""
                    SELECT COUNT(*)
                    FROM ventas v
                    INNER JOIN domicilios d
                        ON d.id_venta=v.id_venta
                    WHERE DATE(v.fecha_venta) BETWEEN ? AND ?
                      AND v.estado='PAGADA'
                """,desde,hasta);

        Integer ventasMostrador=
                Math.max(
                        0,
                        cantidadVentas-
                                ventasDomicilio
                );

        resumen.put(
                "cantidadVentas",
                cantidadVentas
        );

        resumen.put(
                "subtotal",
                subtotal
        );

        resumen.put(
                "descuento",
                descuento
        );

        resumen.put(
                "iva",
                iva
        );

        resumen.put(
                "total",
                total
        );

        resumen.put(
                "ticketPromedio",
                ticketPromedio
        );

        resumen.put(
                "ventasDomicilio",
                ventasDomicilio
        );

        resumen.put(
                "ventasMostrador",
                ventasMostrador
        );

        return resumen;
    }

    private List<Map<String,Object>> obtenerVentasPorDia(
            LocalDate desde,
            LocalDate hasta
    ){

        List<Map<String,Object>> consulta=
                jdbcTemplate.queryForList("""
                    SELECT
                        DATE(fecha_venta) AS fecha,
                        COUNT(*) AS cantidad,
                        COALESCE(SUM(total),0) AS total
                    FROM ventas
                    WHERE DATE(fecha_venta) BETWEEN ? AND ?
                      AND estado='PAGADA'
                    GROUP BY DATE(fecha_venta)
                    ORDER BY DATE(fecha_venta)
                """,desde,hasta);

        List<Map<String,Object>> resultado=
                new ArrayList<>();

        for(Map<String,Object> fila:consulta){

            LocalDate fecha=
                    convertirFecha(
                            fila.get("fecha")
                    );

            Map<String,Object> item=
                    new LinkedHashMap<>();

            item.put(
                    "fecha",
                    fecha!=null
                            ? fecha.toString()
                            : null
            );

            if(fecha!=null){

                String dia=
                        fecha.getDayOfWeek()
                                .getDisplayName(
                                        TextStyle.SHORT,
                                        new Locale(
                                                "es",
                                                "CO"
                                        )
                                );

                item.put(
                        "dia",
                        dia
                );

            }else{

                item.put(
                        "dia",
                        ""
                );
            }

            item.put(
                    "cantidad",
                    numeroEntero(
                            fila.get("cantidad")
                    )
            );

            item.put(
                    "total",
                    numeroDecimal(
                            fila.get("total")
                    )
            );

            resultado.add(item);
        }

        return resultado;
    }

    private List<Map<String,Object>> obtenerMetodosPago(
            LocalDate desde,
            LocalDate hasta
    ){

        return jdbcTemplate.queryForList("""
            SELECT
                metodo_pago AS metodo,
                COUNT(*) AS cantidad,
                COALESCE(SUM(total),0) AS total
            FROM ventas
            WHERE DATE(fecha_venta) BETWEEN ? AND ?
              AND estado='PAGADA'
            GROUP BY metodo_pago
            ORDER BY total DESC
        """,desde,hasta);
    }

    private List<Map<String,Object>> obtenerProductosMasVendidos(
            LocalDate desde,
            LocalDate hasta
    ){

        return jdbcTemplate.queryForList("""
            SELECT
                p.id_producto AS idProducto,
                p.codigo_interno AS codigo,
                p.nombre_comercial AS producto,
                COALESCE(SUM(dv.cantidad),0) AS presentacionesVendidas,
                COALESCE(SUM(dv.unidades_descontadas),0) AS unidadesVendidas,
                COALESCE(SUM(dv.subtotal),0) AS totalVendido
            FROM detalles_ventas dv
            INNER JOIN ventas v
                ON v.id_venta=dv.id_venta
            INNER JOIN productos p
                ON p.id_producto=dv.id_producto
            WHERE DATE(v.fecha_venta) BETWEEN ? AND ?
              AND v.estado='PAGADA'
            GROUP BY
                p.id_producto,
                p.codigo_interno,
                p.nombre_comercial
            ORDER BY
                unidadesVendidas DESC,
                totalVendido DESC
            LIMIT 10
        """,desde,hasta);
    }

    private Map<String,Object> obtenerDomicilios(
            LocalDate desde,
            LocalDate hasta
    ){

        Map<String,Object> resultado=
                new LinkedHashMap<>();

        resultado.put(
                "total",
                consultarEntero("""
                    SELECT COUNT(*)
                    FROM domicilios d
                    INNER JOIN ventas v
                        ON v.id_venta=d.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                """,desde,hasta)
        );

        resultado.put(
                "pendientes",
                consultarEntero("""
                    SELECT COUNT(*)
                    FROM domicilios d
                    INNER JOIN ventas v
                        ON v.id_venta=d.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                      AND d.estado='PENDIENTE'
                """,desde,hasta)
        );

        resultado.put(
                "preparacion",
                consultarEntero("""
                    SELECT COUNT(*)
                    FROM domicilios d
                    INNER JOIN ventas v
                        ON v.id_venta=d.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                      AND d.estado='EN_PREPARACION'
                """,desde,hasta)
        );

        resultado.put(
                "camino",
                consultarEntero("""
                    SELECT COUNT(*)
                    FROM domicilios d
                    INNER JOIN ventas v
                        ON v.id_venta=d.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                      AND d.estado='EN_CAMINO'
                """,desde,hasta)
        );

        resultado.put(
                "entregados",
                consultarEntero("""
                    SELECT COUNT(*)
                    FROM domicilios d
                    INNER JOIN ventas v
                        ON v.id_venta=d.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                      AND d.estado='ENTREGADO'
                """,desde,hasta)
        );

        resultado.put(
                "cancelados",
                consultarEntero("""
                    SELECT COUNT(*)
                    FROM domicilios d
                    INNER JOIN ventas v
                        ON v.id_venta=d.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                      AND d.estado='CANCELADO'
                """,desde,hasta)
        );

        BigDecimal costoDomicilios=
                consultarDecimal("""
                    SELECT
                        COALESCE(
                            SUM(d.costo_domicilio),
                            0
                        )
                    FROM domicilios d
                    INNER JOIN ventas v
                        ON v.id_venta=d.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                      AND d.estado<>'CANCELADO'
                """,desde,hasta);

        resultado.put(
                "ingresosDomicilio",
                costoDomicilios
        );

        return resultado;
    }

    private Map<String,Object> obtenerInventarioActual(){

        Map<String,Object> resultado=
                new LinkedHashMap<>();

        resultado.put(
                "productosActivos",
                consultarEnteroSinParametros("""
                    SELECT COUNT(*)
                    FROM productos
                    WHERE estado='ACTIVO'
                """)
        );

        resultado.put(
                "stockBajo",
                consultarEnteroSinParametros("""
                    SELECT COUNT(*)
                    FROM productos
                    WHERE estado='ACTIVO'
                      AND COALESCE(stock_total,0)>0
                      AND COALESCE(stock_total,0)
                          <=COALESCE(stock_minimo,0)
                """)
        );

        resultado.put(
                "sinStock",
                consultarEnteroSinParametros("""
                    SELECT COUNT(*)
                    FROM productos
                    WHERE estado='ACTIVO'
                      AND COALESCE(stock_total,0)<=0
                """)
        );

        resultado.put(
                "lotesVencidos",
                consultarEnteroSinParametros("""
                    SELECT COUNT(*)
                    FROM lotes
                    WHERE cantidad_actual>0
                      AND fecha_vencimiento<CURDATE()
                      AND estado NOT IN (
                          'RETIRADO',
                          'DEVUELTO'
                      )
                """)
        );

        resultado.put(
                "lotes30Dias",
                consultarEnteroSinParametros("""
                    SELECT COUNT(*)
                    FROM lotes
                    WHERE cantidad_actual>0
                      AND fecha_vencimiento>=CURDATE()
                      AND fecha_vencimiento
                          <=DATE_ADD(
                              CURDATE(),
                              INTERVAL 30 DAY
                          )
                      AND estado NOT IN (
                          'RETIRADO',
                          'DEVUELTO'
                      )
                """)
        );

        resultado.put(
                "lotes90Dias",
                consultarEnteroSinParametros("""
                    SELECT COUNT(*)
                    FROM lotes
                    WHERE cantidad_actual>0
                      AND fecha_vencimiento>
                          DATE_ADD(
                              CURDATE(),
                              INTERVAL 30 DAY
                          )
                      AND fecha_vencimiento
                          <=DATE_ADD(
                              CURDATE(),
                              INTERVAL 90 DAY
                          )
                      AND estado NOT IN (
                          'RETIRADO',
                          'DEVUELTO'
                      )
                """)
        );

        return resultado;
    }

    private List<Map<String,Object>> obtenerDetalleVentas(
            LocalDate desde,
            LocalDate hasta
    ){

        List<Map<String,Object>> consulta=
                jdbcTemplate.queryForList("""
                    SELECT
                        v.id_venta AS idVenta,
                        v.numero_factura AS factura,
                        v.fecha_venta AS fecha,
                        COALESCE(
                            c.nombre_completo,
                            'Cliente ocasional'
                        ) AS cliente,
                        v.subtotal AS subtotal,
                        v.descuento AS descuento,
                        v.impuesto_iva AS iva,
                        v.total AS total,
                        v.metodo_pago AS metodoPago,
                        v.estado AS estado,
                        CASE
                            WHEN d.id_domicilio IS NULL
                                THEN 'MOSTRADOR'
                            ELSE 'DOMICILIO'
                        END AS tipoVenta
                    FROM ventas v
                    LEFT JOIN clientes c
                        ON c.id_cliente=v.id_cliente
                    LEFT JOIN domicilios d
                        ON d.id_venta=v.id_venta
                    WHERE DATE(v.fecha_venta)
                        BETWEEN ? AND ?
                    ORDER BY v.fecha_venta DESC
                    LIMIT 500
                """,desde,hasta);

        List<Map<String,Object>> resultado=
                new ArrayList<>();

        for(Map<String,Object> fila:consulta){

            Map<String,Object> item=
                    new LinkedHashMap<>();

            item.put(
                    "idVenta",
                    fila.get("idVenta")
            );

            item.put(
                    "factura",
                    fila.get("factura")
            );

            item.put(
                    "cliente",
                    fila.get("cliente")
            );

            item.put(
                    "subtotal",
                    fila.get("subtotal")
            );

            item.put(
                    "descuento",
                    fila.get("descuento")
            );

            item.put(
                    "iva",
                    fila.get("iva")
            );

            item.put(
                    "total",
                    fila.get("total")
            );

            item.put(
                    "metodoPago",
                    fila.get("metodoPago")
            );

            item.put(
                    "estado",
                    fila.get("estado")
            );

            item.put(
                    "tipoVenta",
                    fila.get("tipoVenta")
            );

            Object fechaObj=
                    fila.get("fecha");

            if(fechaObj instanceof Timestamp timestamp){

                LocalDateTime fecha=
                        timestamp.toLocalDateTime();

                item.put(
                        "fecha",
                        fecha.toString()
                );

            }else{

                item.put(
                        "fecha",
                        fechaObj!=null
                                ? fechaObj.toString()
                                : null
                );
            }

            resultado.add(item);
        }

        return resultado;
    }

    private Integer consultarEntero(
            String sql,
            Object... parametros
    ){

        Integer valor=
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        parametros
                );

        return valor==null
                ? 0
                : valor;
    }

    private Integer consultarEnteroSinParametros(
            String sql
    ){

        Integer valor=
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class
                );

        return valor==null
                ? 0
                : valor;
    }

    private BigDecimal consultarDecimal(
            String sql,
            Object... parametros
    ){

        BigDecimal valor=
                jdbcTemplate.queryForObject(
                        sql,
                        BigDecimal.class,
                        parametros
                );

        return valor==null
                ? BigDecimal.ZERO
                : valor;
    }

    private int numeroEntero(
            Object valor
    ){

        if(valor==null){
            return 0;
        }

        if(valor instanceof Number numero){
            return numero.intValue();
        }

        return Integer.parseInt(
                valor.toString()
        );
    }

    private BigDecimal numeroDecimal(
            Object valor
    ){

        if(valor==null){
            return BigDecimal.ZERO;
        }

        if(valor instanceof BigDecimal decimal){
            return decimal;
        }

        return new BigDecimal(
                valor.toString()
        );
    }

    private LocalDate convertirFecha(
            Object valor
    ){

        if(valor==null){
            return null;
        }

        if(valor instanceof Date sqlDate){
            return sqlDate.toLocalDate();
        }

        if(valor instanceof Timestamp timestamp){
            return timestamp
                    .toLocalDateTime()
                    .toLocalDate();
        }

        return LocalDate.parse(
                valor.toString()
        );
    }
}