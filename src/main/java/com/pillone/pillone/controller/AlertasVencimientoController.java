package com.pillone.pillone.controller;

import com.pillone.pillone.model.Lotes;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.LotesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/alertas/vencimientos")
public class AlertasVencimientoController {

    private final LotesRepository lotesRepository;
    private final ProductosRepository productosRepository;

    public AlertasVencimientoController(
            LotesRepository lotesRepository,
            ProductosRepository productosRepository
    ){
        this.lotesRepository=lotesRepository;
        this.productosRepository=productosRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(){
        LocalDate hoy=LocalDate.now();
        LocalDate limite=hoy.plusDays(90);

        List<AlertaDTO> alertas=new ArrayList<>();

        for(Lotes lote:lotesRepository.findAll()){
            if(lote.getFechaVencimiento()==null){
                continue;
            }

            if(lote.getCantidadActual()==null || lote.getCantidadActual()<=0){
                continue;
            }

            if("RETIRADO".equalsIgnoreCase(lote.getEstado()) ||
                    "DEVUELTO".equalsIgnoreCase(lote.getEstado())){
                continue;
            }

            if(lote.getFechaVencimiento().isAfter(limite)){
                continue;
            }

            Productos producto=productosRepository.findById(lote.getIdProducto()).orElse(null);

            if(producto==null){
                continue;
            }

            long diasRestantes=ChronoUnit.DAYS.between(hoy,lote.getFechaVencimiento());

            String nivel;

            if(diasRestantes<0){
                nivel="VENCIDO";
            }else if(diasRestantes<=30){
                nivel="CRITICO";
            }else{
                nivel="PROXIMO";
            }

            alertas.add(new AlertaDTO(
                    lote.getIdLote(),
                    producto.getIdProducto(),
                    producto.getNombreComercial(),
                    producto.getConcentracion(),
                    producto.getLaboratorio(),
                    lote.getNumeroLote(),
                    lote.getFechaVencimiento(),
                    lote.getCantidadActual(),
                    diasRestantes,
                    nivel
            ));
        }

        alertas.sort(
                Comparator.comparing(
                        AlertaDTO::getFechaVencimiento
                )
        );

        long vencidos=alertas.stream()
                .filter(a->"VENCIDO".equals(a.getNivel()))
                .count();

        long criticos=alertas.stream()
                .filter(a->"CRITICO".equals(a.getNivel()))
                .count();

        long proximos=alertas.stream()
                .filter(a->"PROXIMO".equals(a.getNivel()))
                .count();

        int unidadesComprometidas=alertas.stream()
                .mapToInt(AlertaDTO::getCantidadActual)
                .sum();

        Map<String,Object> respuesta=new LinkedHashMap<>();

        respuesta.put("fecha",hoy);
        respuesta.put("vencidos",vencidos);
        respuesta.put("criticos",criticos);
        respuesta.put("proximos",proximos);
        respuesta.put("unidadesComprometidas",unidadesComprometidas);
        respuesta.put("alertas",alertas);

        return ResponseEntity.ok(respuesta);
    }

    public static class AlertaDTO {
        private Long idLote;
        private Long idProducto;
        private String producto;
        private String concentracion;
        private String laboratorio;
        private String numeroLote;
        private LocalDate fechaVencimiento;
        private Integer cantidadActual;
        private Long diasRestantes;
        private String nivel;

        public AlertaDTO(
                Long idLote,
                Long idProducto,
                String producto,
                String concentracion,
                String laboratorio,
                String numeroLote,
                LocalDate fechaVencimiento,
                Integer cantidadActual,
                Long diasRestantes,
                String nivel
        ){
            this.idLote=idLote;
            this.idProducto=idProducto;
            this.producto=producto;
            this.concentracion=concentracion;
            this.laboratorio=laboratorio;
            this.numeroLote=numeroLote;
            this.fechaVencimiento=fechaVencimiento;
            this.cantidadActual=cantidadActual;
            this.diasRestantes=diasRestantes;
            this.nivel=nivel;
        }

        public Long getIdLote(){
            return idLote;
        }

        public Long getIdProducto(){
            return idProducto;
        }

        public String getProducto(){
            return producto;
        }

        public String getConcentracion(){
            return concentracion;
        }

        public String getLaboratorio(){
            return laboratorio;
        }

        public String getNumeroLote(){
            return numeroLote;
        }

        public LocalDate getFechaVencimiento(){
            return fechaVencimiento;
        }

        public Integer getCantidadActual(){
            return cantidadActual;
        }

        public Long getDiasRestantes(){
            return diasRestantes;
        }

        public String getNivel(){
            return nivel;
        }
    }
}