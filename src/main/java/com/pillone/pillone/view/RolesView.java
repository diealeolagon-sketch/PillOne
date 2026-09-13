package com.pillone.pillone.view;

import com.pillone.pillone.model.Permisos;
import com.pillone.pillone.model.Roles;
import com.pillone.pillone.repository.PermisosRepository;
import com.pillone.pillone.repository.RolesRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class RolesView {
    private final RolesRepository rolesRepository;
    private final PermisosRepository permisosRepository;

    public RolesView(RolesRepository rolesRepository, PermisosRepository permisosRepository) {
        this.rolesRepository = rolesRepository;
        this.permisosRepository = permisosRepository;
    }

    @GetMapping("/view/roles")
    @Transactional(readOnly = true)
    public String lista(Model model) {
        List<Roles> roles = rolesRepository.findAll();
        roles.sort(Comparator.comparing(Roles::getIdRol));
        model.addAttribute("roles", roles);
        model.addAttribute("totalPermisos", permisosRepository.findByActivoTrueOrderByModuloAscOrdenAscNombreAsc().size());
        return "roles/roles";
    }

    @GetMapping("/view/roles/form")
    public String formCrear(Model model) {
        Roles rol = new Roles();
        cargarFormulario(model, rol, Collections.emptySet());
        return "roles/rolesForm";
    }

    @GetMapping("/view/roles/edit/{id}")
    @Transactional(readOnly = true)
    public String formEditar(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Roles rol = rolesRepository.findById(id).orElse(null);
        if (rol == null) {
            ra.addFlashAttribute("error", "El rol especificado no existe.");
            return "redirect:/view/roles";
        }
        Set<Integer> seleccionados = rol.getPermisos().stream()
                .map(Permisos::getIdPermiso)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        cargarFormulario(model, rol, seleccionados);
        return "roles/rolesForm";
    }

    @PostMapping("/view/roles/save")
    @Transactional
    public String save(
            @ModelAttribute("rol") Roles rol,
            @RequestParam(value = "permissionIds", required = false) List<Integer> permissionIds,
            Model model,
            RedirectAttributes ra
    ) {
        normalizarRol(rol);

        if (rol.getNombre() == null || rol.getNombre().isBlank()) {
            cargarFormulario(model, rol, aSet(permissionIds));
            model.addAttribute("error", "El nombre del rol es obligatorio.");
            return "roles/rolesForm";
        }

        boolean repetido = rol.getIdRol() == null
                ? rolesRepository.existsByNombreIgnoreCase(rol.getNombre())
                : rolesRepository.existsByNombreIgnoreCaseAndIdRolNot(rol.getNombre(), rol.getIdRol());

        if (repetido) {
            cargarFormulario(model, rol, aSet(permissionIds));
            model.addAttribute("error", "Ya existe un rol con ese nombre.");
            return "roles/rolesForm";
        }

        if (permissionIds == null || permissionIds.isEmpty()) {
            cargarFormulario(model, rol, Collections.emptySet());
            model.addAttribute("error", "Selecciona al menos un permiso para el rol.");
            return "roles/rolesForm";
        }

        try {
            Roles destino;
            if (rol.getIdRol() != null) {
                destino = rolesRepository.findById(rol.getIdRol()).orElse(null);
                if (destino == null) {
                    ra.addFlashAttribute("error", "El rol que intentas editar ya no existe.");
                    return "redirect:/view/roles";
                }
            } else {
                destino = new Roles();
            }

            destino.setNombre(rol.getNombre());
            List<Permisos> permisos = permisosRepository.findAllById(permissionIds);
            destino.setPermisos(new LinkedHashSet<>(permisos));
            rolesRepository.saveAndFlush(destino);

            ra.addFlashAttribute(
                    "mensaje",
                    rol.getIdRol() == null
                            ? "Rol creado con sus permisos correctamente."
                            : "Rol y permisos actualizados correctamente."
            );
            return "redirect:/view/roles";

        } catch (DataIntegrityViolationException e) {
            cargarFormulario(model, rol, aSet(permissionIds));
            model.addAttribute("error", "No se pudo guardar el rol. Verifica los datos.");
            return "roles/rolesForm";
        }
    }

    @PostMapping("/view/roles/delete/{id}")
    @Transactional
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        Roles rol = rolesRepository.findById(id).orElse(null);
        if (rol == null) {
            ra.addFlashAttribute("error", "El rol no existe.");
            return "redirect:/view/roles";
        }

        if (rol.getUsuarios() != null && !rol.getUsuarios().isEmpty()) {
            ra.addFlashAttribute(
                    "error",
                    "No se puede eliminar el rol porque está asignado a " + rol.getUsuarios().size() + " usuario(s). Reasigna esos usuarios primero."
            );
            return "redirect:/view/roles";
        }

        try {
            rolesRepository.delete(rol);
            rolesRepository.flush();
            ra.addFlashAttribute("mensaje", "Rol eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("error", "No se puede eliminar el rol porque tiene registros asociados.");
        }
        return "redirect:/view/roles";
    }

    private void cargarFormulario(Model model, Roles rol, Set<Integer> seleccionados) {
        List<Permisos> permisos = permisosRepository.findByActivoTrueOrderByModuloAscOrdenAscNombreAsc();

        LinkedHashMap<String, List<Permisos>> permisosPorModulo = permisos.stream()
                .collect(Collectors.groupingBy(
                        Permisos::getModulo,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        model.addAttribute("rol", rol);
        model.addAttribute("permisosPorModulo", permisosPorModulo);
        model.addAttribute("permisosSeleccionados", seleccionados);
        model.addAttribute("totalPermisos", permisos.size());
    }

    private Set<Integer> aSet(List<Integer> ids) {
        return ids == null ? Collections.emptySet() : new LinkedHashSet<>(ids);
    }

    private void normalizarRol(Roles rol) {
        if (rol.getNombre() == null) return;
        String nombre = rol.getNombre()
                .trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9ÁÉÍÓÚÑ]+", "_")
                .replaceAll("^_+|_+$", "");
        rol.setNombre(nombre);
    }
}
