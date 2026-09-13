package com.pillone.pillone.view;

import com.pillone.pillone.model.Lotes;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.LotesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class AlertasVencimientoView {

    private final LotesRepository lotesRepository;
    private final ProductosRepository productosRepository;

    public AlertasVencimientoView(
            LotesRepository lotesRepository,
            ProductosRepository productosRepository
    ){
        this.lotesRepository=lotesRepository;
        this.productosRepository=productosRepository;
    }

    @GetMapping("/view/alertas/vencimientos")
    public String vencimientos(Model model){
        LocalDate hoy=LocalDate.now();
        LocalDate limite=hoy.plusDays(90);

        List<AlertaVencimiento> alertas=new ArrayList<>();

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

            long diasRestantes=ChronoUnit.DAYS.between(
                    hoy,
                    lote.getFechaVencimiento()
            );

            String nivel;

            if(diasRestantes<0){
                nivel="VENCIDO";
            }else if(diasRestantes<=30){
                nivel="CRITICO";
            }else{
                nivel="PROXIMO";
            }

            alertas.add(
                    new AlertaVencimiento(
                            lote,
                            producto,
                            diasRestantes,
                            nivel
                    )
            );
        }

        alertas.sort(
                Comparator.comparing(
                        alerta->alerta.getLote().getFechaVencimiento()
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
                .mapToInt(a->a.getLote().getCantidadActual()==null
                        ? 0
                        : a.getLote().getCantidadActual())
                .sum();

        model.addAttribute("alertas",alertas);
        model.addAttribute("vencidos",vencidos);
        model.addAttribute("criticos",criticos);
        model.addAttribute("proximos",proximos);
        model.addAttribute("unidadesComprometidas",unidadesComprometidas);
        model.addAttribute("hoy",hoy);

        return "alertas/vencimientos";
    }

    public static class AlertaVencimiento {

        private final Lotes lote;
        private final Productos producto;
        private final long diasRestantes;
        private final String nivel;

        private final int cajasCompletas;
        private final int sellosCompletos;
        private final int unidadesTotales;

        private final int cajasDesglose;
        private final int sellosDesglose;
        private final int unidadesDesglose;

        public AlertaVencimiento(
                Lotes lote,
                Productos producto,
                long diasRestantes,
                String nivel
        ){
            this.lote=lote;
            this.producto=producto;
            this.diasRestantes=diasRestantes;
            this.nivel=nivel;

            int stock=lote.getCantidadActual()==null
                    ? 0
                    : Math.max(0,lote.getCantidadActual());

            int sellosPorCaja=producto.getSellosPorCaja()==null ||
                    producto.getSellosPorCaja()<=0
                    ? 1
                    : producto.getSellosPorCaja();

            int unidadesPorSello=producto.getUnidadesPorSello()==null ||
                    producto.getUnidadesPorSello()<=0
                    ? 1
                    : producto.getUnidadesPorSello();

            int unidadesPorCaja=producto.getUnidadesPorEmpaque()==null ||
                    producto.getUnidadesPorEmpaque()<=0
                    ? sellosPorCaja*unidadesPorSello
                    : producto.getUnidadesPorEmpaque();

            this.unidadesTotales=stock;

            this.cajasCompletas=unidadesPorCaja>0
                    ? stock/unidadesPorCaja
                    : 0;

            this.sellosCompletos=unidadesPorSello>0
                    ? stock/unidadesPorSello
                    : 0;

            this.cajasDesglose=this.cajasCompletas;

            int restoCaja=unidadesPorCaja>0
                    ? stock%unidadesPorCaja
                    : stock;

            this.sellosDesglose=unidadesPorSello>0
                    ? restoCaja/unidadesPorSello
                    : 0;

            this.unidadesDesglose=unidadesPorSello>0
                    ? restoCaja%unidadesPorSello
                    : restoCaja;
        }

        public Lotes getLote(){
            return lote;
        }

        public Productos getProducto(){
            return producto;
        }

        public long getDiasRestantes(){
            return diasRestantes;
        }

        public String getNivel(){
            return nivel;
        }

        public int getCajasCompletas(){
            return cajasCompletas;
        }

        public int getSellosCompletos(){
            return sellosCompletos;
        }

        public int getUnidadesTotales(){
            return unidadesTotales;
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
    }
}