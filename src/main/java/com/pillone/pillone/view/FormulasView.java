package com.pillone.pillone.view;

import com.pillone.pillone.model.DetallesFormulas;
import com.pillone.pillone.model.FormulasMedicas;
import com.pillone.pillone.repository.ClientesRepository;
import com.pillone.pillone.repository.DetallesFormulasRepository;
import com.pillone.pillone.repository.FormulasMedicasRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class FormulasView {

    @Autowired private FormulasMedicasRepository formulasMedicasRepository;
    @Autowired private DetallesFormulasRepository detallesFormulasRepository;
    @Autowired private ClientesRepository clientesRepository;
    @Autowired private ProductosRepository productosRepository;

    @GetMapping("/view/formulas")
    public String lista(Model model){
        model.addAttribute("formulas",formulasMedicasRepository.findAll());
        model.addAttribute("clientes",clientesRepository.findAll());
        return "formulas/formulasMedicas";
    }

    @GetMapping("/view/formulas/form")
    public String form(
            @RequestParam(value="cliente",required=false) Long idCliente,
            @RequestParam(value="producto",required=false) Long idProducto,
            @RequestParam(value="retorno",required=false) String retorno,
            Model model
    ){
        FormulasMedicas formula=new FormulasMedicas();
        formula.setFechaExpedicion(LocalDate.now());
        formula.setVigenciaDias(30);

        if(idCliente!=null){
            formula.setIdCliente(idCliente);
        }

        model.addAttribute("formulaMedica",formula);
        model.addAttribute("productoPreseleccionado",idProducto);
        model.addAttribute("retorno",retorno);
        cargarListas(model);
        return "formulas/formulasMedicasForm";
    }

    @GetMapping("/view/formulas/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ){
        FormulasMedicas formula=formulasMedicasRepository.findById(id).orElse(null);

        if(formula==null){
            ra.addFlashAttribute("error","La fórmula médica no existe.");
            return "redirect:/view/formulas";
        }

        model.addAttribute("formulaMedica",formula);
        model.addAttribute("productoPreseleccionado",null);
        model.addAttribute("retorno",null);
        cargarListas(model);
        return "formulas/formulasMedicasForm";
    }

    @PostMapping("/view/formulas/save")
    @Transactional
    public String save(
            @ModelAttribute("formulaMedica") FormulasMedicas formulaMedica,
            @RequestParam(value="archivo",required=false) MultipartFile archivo,
            @RequestParam(value="idProducto",required=false) Long idProducto,
            @RequestParam(value="dosis",required=false) String dosis,
            @RequestParam(value="frecuencia",required=false) String frecuencia,
            @RequestParam(value="duracionTratamiento",required=false) String duracionTratamiento,
            @RequestParam(value="retorno",required=false) String retorno,
            RedirectAttributes ra
    ){
        try{
            String archivoAnterior=null;

            if(formulaMedica.getIdFormula()!=null){
                FormulasMedicas existente=formulasMedicasRepository
                        .findById(formulaMedica.getIdFormula())
                        .orElse(null);

                if(existente!=null){
                    archivoAnterior=existente.getArchivoAdjuntoUrl();
                }
            }

            if(archivo!=null&&!archivo.isEmpty()){
                formulaMedica.setArchivoAdjuntoUrl(guardarArchivo(archivo));
            }else{
                formulaMedica.setArchivoAdjuntoUrl(archivoAnterior);
            }

            if(formulaMedica.getFechaExpedicion()==null){
                formulaMedica.setFechaExpedicion(LocalDate.now());
            }

            if(formulaMedica.getVigenciaDias()==null||formulaMedica.getVigenciaDias()<=0){
                formulaMedica.setVigenciaDias(30);
            }

            boolean nueva=formulaMedica.getIdFormula()==null;

            if(nueva&&idProducto==null){
                throw new RuntimeException("Debe seleccionar al menos un medicamento para la fórmula.");
            }

            FormulasMedicas guardada=formulasMedicasRepository.save(formulaMedica);

            if(idProducto!=null){
                guardarDetalleFormula(
                        guardada.getIdFormula(),
                        idProducto,
                        dosis,
                        frecuencia,
                        duracionTratamiento
                );
            }

            ra.addFlashAttribute("mensaje","Fórmula médica guardada correctamente.");

            if("ventas".equalsIgnoreCase(retorno)){
                return "redirect:/view/ventas/form?cliente="+guardada.getIdCliente();
            }

            return "redirect:/view/clientes/ver/"+guardada.getIdCliente();

        }catch(Exception e){
            ra.addFlashAttribute(
                    "error",
                    "No se pudo guardar la fórmula médica: "+e.getMessage()
            );

            String destino=formulaMedica.getIdFormula()!=null
                    ? "/view/formulas/edit/"+formulaMedica.getIdFormula()
                    : "/view/formulas/form";

            StringBuilder query=new StringBuilder();

            if(formulaMedica.getIdCliente()!=null){
                query.append("?cliente=").append(formulaMedica.getIdCliente());
            }

            if(idProducto!=null){
                query.append(query.length()==0?"?":"&")
                        .append("producto=").append(idProducto);
            }

            if(retorno!=null&&!retorno.isBlank()){
                query.append(query.length()==0?"?":"&")
                        .append("retorno=").append(retorno);
            }

            return "redirect:"+destino+query;
        }
    }

    @PostMapping("/view/formulas/delete/{id}")
    @Transactional
    public String delete(
            @PathVariable Long id,
            RedirectAttributes ra
    ){
        FormulasMedicas formula=formulasMedicasRepository.findById(id).orElse(null);
        Long idCliente=formula!=null?formula.getIdCliente():null;

        try{
            detallesFormulasRepository.deleteByIdFormula(id);
            formulasMedicasRepository.deleteById(id);
            ra.addFlashAttribute("mensaje","Fórmula médica eliminada correctamente.");
        }catch(Exception e){
            ra.addFlashAttribute("error","No se pudo eliminar la fórmula médica.");
        }

        if(idCliente!=null){
            return "redirect:/view/clientes/ver/"+idCliente;
        }

        return "redirect:/view/formulas";
    }

    @GetMapping("/view/formulas/cliente/{idCliente}")
    public String historialCliente(
            @PathVariable Long idCliente,
            Model model
    ){
        List<FormulasMedicas> formulas=formulasMedicasRepository.findByIdCliente(idCliente);
        LocalDate hoy=LocalDate.now();

        for(FormulasMedicas formula:formulas){
            formula.setEstado(formula.isVigente(hoy)?"VIGENTE":"VENCIDA");
        }

        model.addAttribute("cliente",clientesRepository.findById(idCliente).orElse(null));
        model.addAttribute("formulas",formulas);
        return "formulas/historialCliente";
    }

    @GetMapping("/view/formulas/detalle/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ){
        FormulasMedicas formula=formulasMedicasRepository.findById(id).orElse(null);

        if(formula==null){
            ra.addFlashAttribute("error","La fórmula médica no existe.");
            return "redirect:/view/formulas";
        }

        model.addAttribute("formulaMedica",formula);
        model.addAttribute(
                "detallesFormula",
                detallesFormulasRepository.findByIdFormula(id)
        );
        model.addAttribute("productos",productosRepository.findAll());
        return "formulas/formulaDetalle";
    }

    private void guardarDetalleFormula(
            Long idFormula,
            Long idProducto,
            String dosis,
            String frecuencia,
            String duracionTratamiento
    ){
        String dosisFinal=normalizarTexto(dosis,"Según fórmula médica");
        String frecuenciaFinal=normalizarTexto(frecuencia,"Según fórmula médica");
        String duracionFinal=normalizarTexto(duracionTratamiento,"Según fórmula médica");

        DetallesFormulas detalle=detallesFormulasRepository
                .findByIdFormula(idFormula)
                .stream()
                .filter(d->idProducto.equals(d.getIdProducto()))
                .findFirst()
                .orElseGet(DetallesFormulas::new);

        detalle.setIdFormula(idFormula);
        detalle.setIdProducto(idProducto);
        detalle.setDosis(dosisFinal);
        detalle.setFrecuencia(frecuenciaFinal);
        detalle.setDuracionTratamiento(duracionFinal);
        detallesFormulasRepository.save(detalle);
    }

    private String normalizarTexto(String valor,String porDefecto){
        return valor==null||valor.trim().isEmpty()
                ? porDefecto
                : valor.trim();
    }

    private void cargarListas(Model model){
        model.addAttribute("clientes",clientesRepository.findAll());
        model.addAttribute("productos",productosRepository.findAll());
    }

    private String guardarArchivo(MultipartFile archivo)throws IOException{
        String original=archivo.getOriginalFilename();
        String extension="";

        if(original!=null&&original.lastIndexOf(".")>=0){
            extension=original.substring(original.lastIndexOf(".")).toLowerCase();
        }

        if(!extension.equals(".jpg")
                &&!extension.equals(".jpeg")
                &&!extension.equals(".png")
                &&!extension.equals(".webp")
                &&!extension.equals(".pdf")){
            throw new IOException("Solo se permiten imágenes JPG, PNG, WEBP o archivos PDF.");
        }

        Path carpeta=Paths.get("uploads","formulas");
        Files.createDirectories(carpeta);

        String nombreArchivo=UUID.randomUUID()+extension;
        Path destino=carpeta.resolve(nombreArchivo);

        Files.copy(
                archivo.getInputStream(),
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/uploads/formulas/"+nombreArchivo;
    }
}
