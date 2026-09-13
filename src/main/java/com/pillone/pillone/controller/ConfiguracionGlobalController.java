package com.pillone.pillone.controller;

import com.pillone.pillone.model.ConfiguracionGlobal;
import com.pillone.pillone.repository.ConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionGlobalController {

    @Autowired
    private ConfiguracionRepository configuracionRepository;

    // =====================================================
    // VISTA DE CONFIGURACIÓN
    // =====================================================
    @GetMapping
    public String verConfiguracion(Model model) {
        ConfiguracionGlobal config = configuracionRepository.findById(1L)
                .orElseGet(() -> crearConfiguracionInicial());

        model.addAttribute("configuracion", config);

        List<String> metodosPagoDisponibles = List.of(
                "EFECTIVO",
                "TARJETA_DEBITO",
                "TARJETA_CREDITO",
                "TRANSFERENCIA",
                "NEQUI_DAVIPLATA",
                "PAGO_MIXTO"
        );

        model.addAttribute("metodosPago", metodosPagoDisponibles);

        return "configuracion/configuracion";
    }

    // =====================================================
    // GUARDAR CONFIGURACIÓN GLOBAL
    // =====================================================
    @PostMapping("/guardar")
    public String guardarConfiguracion(
            @ModelAttribute ConfiguracionGlobal configuracion,
            RedirectAttributes redirectAttributes) {

        configuracion.setId(1L);

        if (configuracion.getIvaGeneral() == null) {
            configuracion.setIvaGeneral(new BigDecimal("19.00"));
        }

        if (configuracion.getIvaGeneral().compareTo(BigDecimal.ZERO) < 0) {
            configuracion.setIvaGeneral(BigDecimal.ZERO);
        }

        if (configuracion.getIvaGeneral().compareTo(new BigDecimal("100")) > 0) {
            configuracion.setIvaGeneral(new BigDecimal("100.00"));
        }

        configuracionRepository.save(configuracion);

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "¡Configuración del sistema actualizada con éxito!"
        );

        return "redirect:/configuracion";
    }

    // =====================================================
    // API PARA QUE EL POS LEA CONFIGURACIÓN GLOBAL
    // =====================================================
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<ConfiguracionGlobal> obtenerConfiguracionApi() {

        ConfiguracionGlobal config = configuracionRepository.findById(1L)
                .orElseGet(() -> crearConfiguracionInicial());

        return ResponseEntity.ok(config);
    }

    // =====================================================
    // CONFIGURACIÓN INICIAL
    // SOLO SE USA SI POR ALGÚN MOTIVO NO EXISTE ID = 1
    // =====================================================
    private ConfiguracionGlobal crearConfiguracionInicial() {

        ConfiguracionGlobal config = new ConfiguracionGlobal();

        config.setId(1L);
        config.setNombreFarmacia("FarmaSoft Plus S.A.S.");
        config.setNit("900123456-7");
        config.setIvaGeneral(new BigDecimal("19.00"));
        config.setMoneda("COP");
        config.setTiempoAlertaVencimientoDias(60);

        return configuracionRepository.save(config);
    }
}