package com.pillone.pillone.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardView {

    @GetMapping({"/", "/view/dashboard"})
    public String dashboard(){
        return "dashboard/dashboard";
    }
}