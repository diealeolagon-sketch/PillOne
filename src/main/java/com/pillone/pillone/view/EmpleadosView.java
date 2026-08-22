package com.pillone.pillone.view;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.pillone.pillone.model.Empleados;
import com.pillone.pillone.model.Roles;
import com.pillone.pillone.model.Usuarios;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.RolesRepository;
import com.pillone.pillone.repository.SucursalesRepository;
import com.pillone.pillone.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmpleadosView {

    @Autowired
    private EmpleadosRepository empleadosRepository;

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    // LISTA
    @GetMapping("/view/empleados")
    public String lista(Model model) {
        model.addAttribute("empleados", empleadosRepository.findAll());
        return "empleados/empleados";
    }

    // FORMULARIO CREAR
    @GetMapping("/view/empleados/form")
    public String form(Model model) {
        Empleados empleado = new Empleados();
        model.addAttribute("empleado", empleado);
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        model.addAttribute("roles", rolesRepository.findAll());
        return "empleados/empleadosForm";
    }

    @PostMapping("/view/empleados/save")
    public String save(
            @Valid @ModelAttribute Empleados empleado,
                       BindingResult result,
                       @RequestParam(value = "password_hash", required = false) String passwordHash,
                       RedirectAttributes ra) {

        if (result.hasErrors())
        {
            ra.addFlashAttribute("error", "hay campos obligatorios vacios.");
            return "redirect:/view/empleados/form";
        }

        if (empleado.getId_empleado() == null &&
                empleadosRepository.existsByNumeroDocumento(
                        empleado.getNumero_documento()))
        {

            ra.addFlashAttribute(
                    "error",
                    "Ese número de documento ya está registrado."
            );

            return "redirect:/view/empleados/form";
        }


        // 2. Guardar primero el empleado para obtener/asegurar su ID
        Empleados empleadoGuardado = empleadosRepository.save(empleado);



        ra.addFlashAttribute("mensaje", "Empleado y usuario guardados con éxito");
        return "redirect:/view/empleados";
    }

    // EDITAR
    @GetMapping("/view/empleados/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Empleados empleado = empleadosRepository.findById(id).orElse(null);
        if (empleado == null) return "redirect:/view/empleados";

        model.addAttribute("empleado", empleado);
        model.addAttribute("sucursales", sucursalesRepository.findAll());
        model.addAttribute("roles", rolesRepository.findAll());
        return "empleados/empleadosForm";
    }

    // ELIMINAR
    @PostMapping("/view/empleados/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        empleadosRepository.deleteById(id);
        ra.addFlashAttribute("mensaje", "Empleado eliminado");
        return "redirect:/view/empleados";
    }
}