package com.pillone.pillone.controller;
import com.pillone.pillone.model.FormulasMedicas;
import com.pillone.pillone.service.FormulasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/formulas")
public class FormulasController {
    @Autowired private FormulasService formulasService;

    @GetMapping("/validar")
    public ResponseEntity<Map<String,Object>> validar(@RequestParam Long clienteId,@RequestParam Long productoId){
        FormulasMedicas formula=formulasService.obtenerFormulaVigente(clienteId,productoId);
        Map<String,Object> respuesta=new LinkedHashMap<>();
        respuesta.put("vigente",formula!=null);
        if(formula!=null){
            respuesta.put("idFormula",formula.getIdFormula());
            respuesta.put("medico",formula.getNombreMedico());
            respuesta.put("fechaExpedicion",formula.getFechaExpedicion());
            respuesta.put("fechaVencimiento",formula.getFechaVencimiento());
        }
        return ResponseEntity.ok(respuesta);
    }
}
