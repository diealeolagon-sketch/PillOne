package com.pillone.pillone.controller;

import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ProductosControlle
{

    //Inyectamos dependencias (ProductosRepository)
    @Autowired
    private ProductosRepository productosRepository;


}
