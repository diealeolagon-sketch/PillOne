package com.pillone.pillone.view;

import com.pillone.pillone.model.Empleados;
import com.pillone.pillone.model.Sucursales;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.SucursalesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/view/sucursales")
public class SucursalesView {

    private final SucursalesRepository sucursalesRepository;
    private final EmpleadosRepository empleadosRepository;

    public SucursalesView(
            SucursalesRepository sucursalesRepository,
            EmpleadosRepository empleadosRepository
    ){
        this.sucursalesRepository=sucursalesRepository;
        this.empleadosRepository=empleadosRepository;
    }

    @GetMapping
    public String lista(Model model){

        List<Sucursales> sucursales=
                sucursalesRepository.findAllByOrderByEstadoAscNombreAsc();

        model.addAttribute("sucursales",sucursales);
        model.addAttribute(
                "totalSucursales",
                sucursalesRepository.count()
        );
        model.addAttribute(
                "sucursalesActivas",
                sucursalesRepository.countByEstado("ACTIVA")
        );
        model.addAttribute(
                "sucursalesInactivas",
                sucursalesRepository.countByEstado("INACTIVA")
        );
        model.addAttribute(
                "totalEmpleados",
                empleadosRepository.count()
        );

        return "sucursales/sucursales";
    }

    @GetMapping("/form")
    public String form(Model model){

        if(!model.containsAttribute("sucursal")){
            model.addAttribute(
                    "sucursal",
                    new Sucursales()
            );
        }

        return "sucursales/sucursalesForm";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ){

        Sucursales sucursal=
                sucursalesRepository.findById(id).orElse(null);

        if(sucursal==null){
            ra.addFlashAttribute(
                    "error",
                    "La sucursal solicitada no existe."
            );

            return "redirect:/view/sucursales";
        }

        model.addAttribute(
                "sucursal",
                sucursal
        );

        return "sucursales/sucursalesForm";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute Sucursales sucursal,
            RedirectAttributes ra
    ){

        try{

            if(sucursal.getNombre()==null||
                    sucursal.getNombre().trim().isEmpty()){

                throw new IllegalArgumentException(
                        "El nombre de la sucursal es obligatorio."
                );
            }

            if(sucursal.getCodigo()==null||
                    sucursal.getCodigo().trim().isEmpty()){

                throw new IllegalArgumentException(
                        "El código de la sucursal es obligatorio."
                );
            }

            if(sucursal.getDireccion()==null||
                    sucursal.getDireccion().trim().isEmpty()){

                throw new IllegalArgumentException(
                        "La dirección es obligatoria."
                );
            }

            String codigo=
                    sucursal.getCodigo().trim().toUpperCase();

            sucursal.setCodigo(codigo);

            boolean codigoExiste;

            if(sucursal.getIdSucursal()==null){

                codigoExiste=
                        sucursalesRepository
                                .existsByCodigoIgnoreCase(codigo);

            }else{

                codigoExiste=
                        sucursalesRepository
                                .existsByCodigoIgnoreCaseAndIdSucursalNot(
                                        codigo,
                                        sucursal.getIdSucursal()
                                );
            }

            if(codigoExiste){

                throw new IllegalArgumentException(
                        "Ya existe una sucursal con el código "+codigo+"."
                );
            }

            if(sucursal.getCiudad()==null||
                    sucursal.getCiudad().isBlank()){

                sucursal.setCiudad("Tuluá");
            }

            if(sucursal.getDepartamento()==null||
                    sucursal.getDepartamento().isBlank()){

                sucursal.setDepartamento(
                        "Valle del Cauca"
                );
            }

            if(sucursal.getEstado()==null||
                    sucursal.getEstado().isBlank()){

                sucursal.setEstado("ACTIVA");
            }

            boolean nueva=
                    sucursal.getIdSucursal()==null;

            sucursalesRepository.save(sucursal);

            ra.addFlashAttribute(
                    "mensaje",
                    nueva
                            ? "Sucursal registrada correctamente."
                            : "Sucursal actualizada correctamente."
            );

            return "redirect:/view/sucursales";

        }catch(Exception e){

            ra.addFlashAttribute(
                    "error",
                    e.getMessage()!=null
                            ? e.getMessage()
                            : "No fue posible guardar la sucursal."
            );

            if(sucursal.getIdSucursal()!=null){

                return "redirect:/view/sucursales/edit/"
                        +sucursal.getIdSucursal();
            }

            return "redirect:/view/sucursales/form";
        }
    }

    @PostMapping("/estado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            RedirectAttributes ra
    ){

        Sucursales sucursal=
                sucursalesRepository.findById(id).orElse(null);

        if(sucursal==null){

            ra.addFlashAttribute(
                    "error",
                    "La sucursal no existe."
            );

            return "redirect:/view/sucursales";
        }

        String nuevoEstado=
                "ACTIVA".equalsIgnoreCase(
                        sucursal.getEstado()
                )
                        ? "INACTIVA"
                        : "ACTIVA";

        sucursal.setEstado(nuevoEstado);

        sucursalesRepository.save(sucursal);

        ra.addFlashAttribute(
                "mensaje",
                "La sucursal quedó "
                        +nuevoEstado.toLowerCase()+"."
        );

        return "redirect:/view/sucursales";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ){

        Sucursales sucursal=
                sucursalesRepository.findById(id).orElse(null);

        if(sucursal==null){

            ra.addFlashAttribute(
                    "error",
                    "La sucursal solicitada no existe."
            );

            return "redirect:/view/sucursales";
        }

        List<Empleados> empleados=
                empleadosRepository
                        .findBySucursal_IdSucursal(id);

        model.addAttribute(
                "sucursal",
                sucursal
        );

        model.addAttribute(
                "empleadosSucursal",
                empleados
        );

        model.addAttribute(
                "cantidadEmpleados",
                empleados.size()
        );

        return "sucursales/sucursalDetalle";
    }
}