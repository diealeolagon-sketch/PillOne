package com.pillone.pillone.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportesView {

    @GetMapping("/view/reportes")
    public String reportes(){
        return "reportes/reportes";
    }
}