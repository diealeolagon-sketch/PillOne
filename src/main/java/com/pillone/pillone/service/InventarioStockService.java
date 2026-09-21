package com.pillone.pillone.service;

import com.pillone.pillone.model.Lotes;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.LotesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InventarioStockService {

    private final LotesRepository lotesRepository;
    private final ProductosRepository productosRepository;

    public InventarioStockService(
            LotesRepository lotesRepository,
            ProductosRepository productosRepository
    ){
        this.lotesRepository=lotesRepository;
        this.productosRepository=productosRepository;
    }

    public int stockDisponible(Long idProducto){
        Integer stock=lotesRepository.sumarStockDisponible(idProducto,LocalDate.now());
        return stock==null ? 0 : stock;
    }

    @Transactional
    public int sincronizarProducto(Long idProducto){
        if(idProducto==null) return 0;

        actualizarEstadosProducto(idProducto);
        int stock=stockDisponible(idProducto);

        Productos producto=productosRepository.findById(idProducto).orElse(null);
        if(producto!=null){
            producto.setStockTotal(stock);
            productosRepository.save(producto);
        }
        return stock;
    }

    @Transactional
    public void sincronizarTodo(){
        LocalDate hoy=LocalDate.now();
        List<Lotes> lotes=lotesRepository.findAll();
        Set<Long> productosAfectados=new HashSet<>();

        for(Lotes lote:lotes){
            if(lote.getIdProducto()!=null) productosAfectados.add(lote.getIdProducto());
            actualizarEstadoAutomatico(lote,hoy);
        }

        for(Productos producto:productosRepository.findAll()){
            productosAfectados.add(producto.getIdProducto());
        }

        for(Long idProducto:productosAfectados){
            int stock=stockDisponible(idProducto);
            productosRepository.findById(idProducto).ifPresent(producto->{
                producto.setStockTotal(stock);
                productosRepository.save(producto);
            });
        }
    }

    private void actualizarEstadosProducto(Long idProducto){
        LocalDate hoy=LocalDate.now();
        for(Lotes lote:lotesRepository.findByIdProductoOrderByFechaVencimientoAscIdLoteAsc(idProducto)){
            actualizarEstadoAutomatico(lote,hoy);
        }
    }

    private void actualizarEstadoAutomatico(Lotes lote,LocalDate hoy){
        if(lote==null) return;

        String estado=lote.getEstado()==null ? "" : lote.getEstado().trim().toUpperCase();

        // RETIRADO y DEVUELTO son estados operativos manuales y nunca se reactivan solos.
        if("RETIRADO".equals(estado) || "DEVUELTO".equals(estado)) return;

        String nuevo=estadoSegunVencimiento(lote.getFechaVencimiento(),hoy);
        if(!nuevo.equals(estado)){
            lote.setEstado(nuevo);
            lotesRepository.save(lote);
        }
    }

    public String estadoSegunVencimiento(LocalDate vencimiento){
        return estadoSegunVencimiento(vencimiento,LocalDate.now());
    }

    private String estadoSegunVencimiento(LocalDate vencimiento,LocalDate hoy){
        if(vencimiento==null) return "DISPONIBLE";
        if(vencimiento.isBefore(hoy)) return "VENCIDO";

        long dias=ChronoUnit.DAYS.between(hoy,vencimiento);
        return dias<=90 ? "PROXIMO_A_VENCER" : "DISPONIBLE";
    }
}
