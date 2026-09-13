package com.pillone.pillone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessController {
    @GetMapping("/acceso-denegado")
    public String accesoDenegado(){
        return "auth/accesoDenegado";
    }
}
