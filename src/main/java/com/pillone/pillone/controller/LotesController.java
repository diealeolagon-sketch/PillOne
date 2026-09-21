package com.pillone.pillone.controller;

import com.pillone.pillone.model.Lotes;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.LotesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import com.pillone.pillone.service.InventarioStockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lotes")
public class LotesController {

    private final LotesRepository lotesRepository;
    private final ProductosRepository productosRepository;
    private final InventarioStockService inventarioStockService;

    public LotesController(
            LotesRepository lotesRepository,
            ProductosRepository productosRepository,
            InventarioStockService inventarioStockService
    ){
        this.lotesRepository=lotesRepository;
        this.productosRepository=productosRepository;
        this.inventarioStockService=inventarioStockService;
    }

    @GetMapping
    public List<Lotes> listar(){
        return lotesRepository.findAll();
    }

    @GetMapping("/producto/{idProducto}")
    public List<Lotes> listarPorProducto(@PathVariable Long idProducto){
        return lotesRepository
                .findByIdProductoOrderByFechaVencimientoAscIdLoteAsc(idProducto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id){
        return lotesRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(()->
                        ResponseEntity.notFound().build()
                );
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado
    ){
        Lotes lote=lotesRepository.findById(id).orElse(null);

        if(lote==null){
            return ResponseEntity.notFound().build();
        }

        if(estado==null || estado.isBlank()){
            return ResponseEntity.badRequest()
                    .body(Map.of("error","Debe indicar un estado."));
        }

        String nuevoEstado=estado.trim().toUpperCase();

        if("RETIRADO".equals(nuevoEstado)){
            lote.setEstado("RETIRADO");
        }else if("DEVUELTO".equals(nuevoEstado)){
            lote.setEstado("DEVUELTO");
        }else if("DISPONIBLE".equals(nuevoEstado)){
            if(lote.getFechaVencimiento()==null){
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "El lote no tiene fecha de vencimiento."
                        ));
            }

            if(lote.getFechaVencimiento().isBefore(LocalDate.now())){
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error",
                                "Un lote vencido no puede reactivarse."
                        ));
            }

            lote.setEstado(
                    calcularEstado(lote.getFechaVencimiento())
            );
        }else{
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            "Solo puede retirar, devolver o reactivar un lote."
                    ));
        }

        lotesRepository.save(lote);
        sincronizarStockProducto(lote.getIdProducto());

        Map<String,Object> respuesta=new LinkedHashMap<>();
        respuesta.put("mensaje","Estado actualizado correctamente.");
        respuesta.put("idLote",lote.getIdLote());
        respuesta.put("estado",lote.getEstado());

        return ResponseEntity.ok(respuesta);
    }

    private String calcularEstado(LocalDate vencimiento){
        if(vencimiento==null){
            return "DISPONIBLE";
        }

        LocalDate hoy=LocalDate.now();

        if(vencimiento.isBefore(hoy)){
            return "VENCIDO";
        }

        long dias=ChronoUnit.DAYS.between(hoy,vencimiento);

        return dias<=90
                ? "PROXIMO_A_VENCER"
                : "DISPONIBLE";
    }

    private void sincronizarStockProducto(Long idProducto){
        inventarioStockService.sincronizarProducto(idProducto);
    }
}