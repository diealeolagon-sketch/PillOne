package com.pillone.pillone.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/domicilios")
public class DomiciliosView {

    @GetMapping
    public String domicilios(){
        return "domicilios/domicilios";
    }
}