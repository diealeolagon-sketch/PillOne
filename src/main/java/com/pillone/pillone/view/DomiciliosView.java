package com.pillone.pillone.view;

import com.pillone.pillone.model.Domicilios;
import com.pillone.pillone.repository.DomiciliosRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/view/domicilios")
public class DomiciliosView {
    private final DomiciliosRepository repo;
    private final JdbcTemplate jdbc;

    public DomiciliosView(DomiciliosRepository repo,JdbcTemplate jdbc){
        this.repo=repo;
        this.jdbc=jdbc;
    }

    @GetMapping
    @Transactional(readOnly=true)
    public String lista(Model model){
        model.addAttribute("domicilios",repo.findAllByOrderByIdDomicilioDesc());

        /*
         * Una devolución parcial no debe cerrar el domicilio: todavía puede
         * quedar mercancía por entregar o devolver. Por eso conservamos
         * EN_CAMINO en BD y enviamos al HTML las ventas que ya tienen al menos
         * una devolución para mostrarlas como "DEVOLUCIÓN PARCIAL".
         */
        model.addAttribute("ventasConDevolucion",jdbc.queryForList("""
            SELECT DISTINCT id_venta
            FROM devoluciones
            WHERE tipo_devolucion='CLIENTE'
              AND id_venta IS NOT NULL
        """).stream()
                .map(fila -> ((Number)fila.get("id_venta")).longValue())
                .toList());

        model.addAttribute("detallesVentas",jdbc.queryForList("""
            SELECT dv.id_detalle_venta,dv.id_venta,dv.id_producto,dv.id_lote,
                   p.nombre_comercial,p.codigo_interno,l.numero_lote,
                   dv.unidades_descontadas AS cantidad_vendida,
                   GREATEST(
                       dv.unidades_descontadas-COALESCE((
                           SELECT SUM(dev.cantidad)
                           FROM devoluciones dev
                           WHERE dev.tipo_devolucion='CLIENTE'
                             AND dev.id_venta=dv.id_venta
                             AND dev.id_producto=dv.id_producto
                             AND dev.id_lote=dv.id_lote
                       ),0),0
                   ) AS cantidad_disponible_devolver
            FROM detalles_ventas dv
            JOIN productos p ON p.id_producto=dv.id_producto
            JOIN lotes l ON l.id_lote=dv.id_lote
            ORDER BY dv.id_venta,dv.id_detalle_venta
            """));

        return "domicilios/domicilios";
    }

    @PostMapping("/avanzar/{id}")
    @Transactional
    public String avanzar(@PathVariable Long id,RedirectAttributes ra){
        Domicilios d=repo.findById(id).orElse(null);

        if(d==null){
            ra.addFlashAttribute("error","El domicilio no existe.");
            return "redirect:/view/domicilios";
        }

        String actual=d.getEstado()==null?"PENDIENTE":d.getEstado().toUpperCase();

        String siguiente=switch(actual){
            case "PENDIENTE" -> "EN_PREPARACION";
            case "EN_PREPARACION" -> "EN_CAMINO";
            default -> null;
        };

        if(siguiente==null){
            ra.addFlashAttribute("error","Este domicilio ya no puede avanzar desde aquí.");
            return "redirect:/view/domicilios";
        }

        d.setEstado(siguiente);

        if("EN_CAMINO".equals(siguiente)&&d.getFechaHoraSalida()==null){
            d.setFechaHoraSalida(LocalDateTime.now());
        }

        repo.save(d);

        ra.addFlashAttribute("mensaje","Domicilio actualizado a "+siguiente.replace('_',' ')+".");
        return "redirect:/view/domicilios";
    }

    @PostMapping("/entregar/{id}")
    @Transactional
    public String entregar(
            @PathVariable Long id,
            @RequestParam MultipartFile fotoEntrega,
            @RequestParam(required=false) String recibidoPor,
            @RequestParam(required=false) String observacionesEntrega,
            @RequestParam(defaultValue="false") Boolean pagado,
            RedirectAttributes ra
    ){
        Domicilios d=repo.findById(id).orElse(null);

        if(d==null){
            ra.addFlashAttribute("error","El domicilio no existe.");
            return "redirect:/view/domicilios";
        }

        if(!"EN_CAMINO".equalsIgnoreCase(d.getEstado())){
            ra.addFlashAttribute("error","Solo puedes finalizar un domicilio que esté EN CAMINO.");
            return "redirect:/view/domicilios";
        }

        if(fotoEntrega==null||fotoEntrega.isEmpty()){
            ra.addFlashAttribute("error","Debes tomar o subir una foto para confirmar la entrega.");
            return "redirect:/view/domicilios";
        }

        try{
            d.setEvidenciaEntrega(guardarFoto(d.getIdDomicilio(),fotoEntrega));
        }catch(IOException e){
            ra.addFlashAttribute("error",e.getMessage());
            return "redirect:/view/domicilios";
        }

        StringBuilder obs=new StringBuilder();

        if(recibidoPor!=null&&!recibidoPor.trim().isEmpty()){
            obs.append("Recibido por: ").append(recibidoPor.trim());
        }

        if(observacionesEntrega!=null&&!observacionesEntrega.trim().isEmpty()){
            if(!obs.isEmpty()) obs.append(" | ");
            obs.append(observacionesEntrega.trim());
        }

        d.setObservacionesEntrega(obs.isEmpty()?null:obs.toString());

        boolean pago=Boolean.TRUE.equals(pagado);
        d.setPagado(pago);

        if(pago&&d.getFechaPago()==null){
            d.setFechaPago(LocalDateTime.now());
        }

        d.setEstado("ENTREGADO");

        if(d.getFechaHoraSalida()==null){
            d.setFechaHoraSalida(LocalDateTime.now());
        }

        d.setFechaHoraEntrega(LocalDateTime.now());
        repo.save(d);

        ra.addFlashAttribute("mensaje","Entrega confirmada correctamente con evidencia.");
        return "redirect:/view/domicilios";
    }

    @PostMapping("/cancelar/{id}")
    @Transactional
    public String cancelar(@PathVariable Long id,RedirectAttributes ra){
        Domicilios d=repo.findById(id).orElse(null);

        if(d==null){
            ra.addFlashAttribute("error","El domicilio no existe.");
            return "redirect:/view/domicilios";
        }

        if("ENTREGADO".equalsIgnoreCase(d.getEstado())){
            ra.addFlashAttribute("error","Un domicilio entregado no puede cancelarse.");
            return "redirect:/view/domicilios";
        }

        d.setEstado("CANCELADO");
        repo.save(d);

        ra.addFlashAttribute("mensaje","Domicilio cancelado.");
        return "redirect:/view/domicilios";
    }

    private String guardarFoto(Long idDomicilio,MultipartFile archivo)throws IOException{
        String contentType=archivo.getContentType()==null?"":archivo.getContentType().toLowerCase();

        if(!contentType.startsWith("image/")){
            throw new IOException("La evidencia debe ser una imagen.");
        }

        String original=archivo.getOriginalFilename();
        String extension=".jpg";

        if(original!=null&&original.lastIndexOf(".")>=0){
            extension=original.substring(original.lastIndexOf(".")).toLowerCase();
        }

        if(!Set.of(".jpg",".jpeg",".png",".webp").contains(extension)){
            throw new IOException("Solo se permiten imágenes JPG, JPEG, PNG o WEBP.");
        }

        Path carpeta=Paths.get("uploads","domicilios");
        Files.createDirectories(carpeta);

        String nombre="domicilio_"+idDomicilio+"_"+UUID.randomUUID()+extension;
        Path destino=carpeta.resolve(nombre);

        Files.copy(
                archivo.getInputStream(),
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/uploads/domicilios/"+nombre;
    }
}
