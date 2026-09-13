package com.pillone.pillone.view;

import com.pillone.pillone.model.Clientes;
import com.pillone.pillone.repository.ClientesRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientesView {

    @Autowired
    private ClientesRepository clientesRepository;

    @GetMapping("/view/clientes")
    public String lista(Model model) {
        model.addAttribute("clientes", clientesRepository.findAll());
        return "clientes/clientes";
    }

    @GetMapping("/view/clientes/form")
    public String form(Model model) {
        model.addAttribute("cliente", new Clientes());
        return "clientes/clientesForm";
    }

    @GetMapping("/view/clientes/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ) {
        Clientes cliente = clientesRepository.findById(id).orElse(null);

        if (cliente == null) {
            ra.addFlashAttribute(
                    "error",
                    "El cliente no existe."
            );

            return "redirect:/view/clientes";
        }

        model.addAttribute("cliente", cliente);

        return "clientes/clientesForm";
    }

    @PostMapping("/view/clientes/save")
    public String save(
            @Valid @ModelAttribute("cliente") Clientes cliente,
            BindingResult result,
            @RequestParam(value = "accion", defaultValue = "guardar") String accion,
            Model model,
            RedirectAttributes ra
    ) {
        if (result.hasErrors()) {
            return "clientes/clientesForm";
        }

        boolean nuevo = cliente.getId_cliente() == null;

        try {
            Clientes guardado = clientesRepository.save(cliente);

            /*
             * GUARDAR Y CREAR FORMULA
             */
            if ("formula".equals(accion)) {

                ra.addFlashAttribute(
                        "mensaje",
                        nuevo
                                ? "Cliente registrado. Ahora puede crear su fórmula médica."
                                : "Cliente actualizado. Ahora puede crear su fórmula médica."
                );

                return "redirect:/view/formulas/form?cliente="
                        + guardado.getId_cliente();
            }

            /*
             * GUARDAR NORMAL
             */
            ra.addFlashAttribute(
                    "mensaje",
                    nuevo
                            ? "Cliente registrado correctamente."
                            : "Cliente actualizado correctamente."
            );

            return "redirect:/view/clientes";

        } catch (DataIntegrityViolationException e) {

            model.addAttribute(
                    "error",
                    "No se pudo guardar el cliente. Verifica que el número de documento no esté registrado."
            );

            return "clientes/clientesForm";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Ocurrió un error al guardar el cliente: "
                            + e.getMessage()
            );

            return "clientes/clientesForm";
        }
    }

    @PostMapping("/view/clientes/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes ra
    ) {
        try {
            if (!clientesRepository.existsById(id)) {

                ra.addFlashAttribute(
                        "error",
                        "El cliente no existe."
                );

                return "redirect:/view/clientes";
            }

            clientesRepository.deleteById(id);

            ra.addFlashAttribute(
                    "mensaje",
                    "Cliente eliminado correctamente."
            );

        } catch (DataIntegrityViolationException e) {

            ra.addFlashAttribute(
                    "error",
                    "No se puede eliminar el cliente porque tiene ventas, fórmulas u otra información relacionada."
            );

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    "No fue posible eliminar el cliente."
            );
        }

        return "redirect:/view/clientes";
    }
}