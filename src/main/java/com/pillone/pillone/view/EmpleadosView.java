package com.pillone.pillone.view;

import com.pillone.pillone.model.Empleados;
import com.pillone.pillone.model.Roles;
import com.pillone.pillone.model.Usuarios;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.RolesRepository;
import com.pillone.pillone.repository.SucursalesRepository;
import com.pillone.pillone.repository.UsuariosRepository;
import com.pillone.pillone.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EmpleadosView {
    private final EmpleadosRepository empleadosRepository;
    private final SucursalesRepository sucursalesRepository;
    private final RolesRepository rolesRepository;
    private final UsuariosRepository usuariosRepository;
    private final PasswordService passwordService;

    public EmpleadosView(
            EmpleadosRepository empleadosRepository,
            SucursalesRepository sucursalesRepository,
            RolesRepository rolesRepository,
            UsuariosRepository usuariosRepository,
            PasswordService passwordService
    ){
        this.empleadosRepository=empleadosRepository;
        this.sucursalesRepository=sucursalesRepository;
        this.rolesRepository=rolesRepository;
        this.usuariosRepository=usuariosRepository;
        this.passwordService=passwordService;
    }

    @GetMapping("/view/empleados")
    public String lista(Model model){
        Map<Long,Usuarios> usuariosPorEmpleado=new HashMap<>();
        for(Usuarios usuario:usuariosRepository.findAll()){
            if(usuario.getIdEmpleado()!=null) usuariosPorEmpleado.put(usuario.getIdEmpleado(),usuario);
        }
        model.addAttribute("empleados",empleadosRepository.findAll());
        model.addAttribute("usuariosPorEmpleado",usuariosPorEmpleado);
        return "empleados/empleados";
    }

    @GetMapping("/view/empleados/form")
    public String form(Model model){
        Empleados empleado=new Empleados();
        empleado.setEstado("ACTIVO");
        model.addAttribute("empleado",empleado);
        model.addAttribute("usuario",new Usuarios());
        cargarListas(model);
        return "empleados/empleadosForm";
    }

    @GetMapping("/view/empleados/edit/{id}")
    public String edit(@PathVariable Long id,Model model,RedirectAttributes ra){
        Empleados empleado=empleadosRepository.findById(id).orElse(null);
        if(empleado==null){
            ra.addFlashAttribute("error","El empleado no existe.");
            return "redirect:/view/empleados";
        }

        Usuarios usuario=usuariosRepository.findByIdEmpleado(id);
        if(usuario==null){
            usuario=new Usuarios();
            usuario.setIdEmpleado(id);
        }

        model.addAttribute("empleado",empleado);
        model.addAttribute("usuario",usuario);
        cargarListas(model);
        return "empleados/empleadosForm";
    }

    @PostMapping("/view/empleados/save")
    @Transactional
    public String save(
            @Valid @ModelAttribute("empleado") Empleados empleado,
            BindingResult result,
            @RequestParam String username,
            @RequestParam(required=false) String password,
            @RequestParam(required=false) String confirmarPassword,
            @RequestParam Integer idRol,
            @RequestParam(defaultValue="ACTIVO") String estadoUsuario,
            Model model,
            RedirectAttributes ra
    ){
        normalizarEmpleado(empleado);
        username=username==null?"":username.trim();

        Usuarios usuarioExistente=empleado.getId_empleado()==null
                ?null
                :usuariosRepository.findByIdEmpleado(empleado.getId_empleado());

        if(result.hasErrors()){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "Hay campos obligatorios vacíos o incorrectos.");
        }

        if(empleado.getCorreo()==null || empleado.getCorreo().isBlank()){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "El correo del empleado es obligatorio para recuperación de contraseña.");
        }

        if(empleado.getId_empleado()==null){
            if(empleadosRepository.existsByNumeroDocumento(empleado.getNumero_documento())){
                return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                        "Ese número de documento ya está registrado.");
            }
        }else if(empleadosRepository.existsByNumeroDocumentoAndIdEmpleadoNot(
                empleado.getNumero_documento(),empleado.getId_empleado())){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "Ese número de documento pertenece a otro empleado.");
        }

        if(username.isBlank()){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "El nombre de usuario es obligatorio.");
        }

        if(username.length()<4 || username.length()>50){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "El usuario debe tener entre 4 y 50 caracteres.");
        }

        if(!username.matches("^[A-Za-z0-9._-]+$")){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "El usuario solo puede contener letras, números, punto, guion y guion bajo.");
        }

        boolean usernameOcupado=usuarioExistente==null
                ?usuariosRepository.existsByUsernameIgnoreCase(username)
                :usuariosRepository.existsByUsernameIgnoreCaseAndIdUsuarioNot(username,usuarioExistente.getIdUsuario());

        if(usernameOcupado){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "Ese nombre de usuario ya está registrado.");
        }

        Roles rol=rolesRepository.findById(idRol).orElse(null);
        if(rol==null){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "Selecciona un rol válido.");
        }

        boolean creandoUsuario=usuarioExistente==null;
        boolean cambioPassword=password!=null && !password.isBlank();

        if(creandoUsuario && !cambioPassword){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "La contraseña es obligatoria al crear el acceso del empleado.");
        }

        if(cambioPassword){
            if(password.length()<8){
                return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                        "La contraseña debe tener mínimo 8 caracteres.");
            }
            if(confirmarPassword==null || !password.equals(confirmarPassword)){
                return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                        "Las contraseñas no coinciden.");
            }
        }

        Usuarios.EstadoUsuario estado;
        try{
            estado=Usuarios.EstadoUsuario.valueOf(estadoUsuario.toUpperCase());
        }catch(Exception e){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "Estado de usuario no válido.");
        }

        try{
            Empleados empleadoGuardado=empleadosRepository.saveAndFlush(empleado);
            Usuarios usuario=usuarioExistente==null?new Usuarios():usuarioExistente;
            usuario.setIdEmpleado(empleadoGuardado.getId_empleado());
            usuario.setUsername(username);
            usuario.setRol(rol);
            usuario.setEstado("ACTIVO".equalsIgnoreCase(empleadoGuardado.getEstado())
                    ?estado
                    :Usuarios.EstadoUsuario.INACTIVO);

            if(cambioPassword) usuario.setPasswordHash(passwordService.hash(password));
            usuariosRepository.saveAndFlush(usuario);

            ra.addFlashAttribute("mensaje",usuarioExistente==null
                    ?"Empleado y credenciales creados correctamente."
                    :"Empleado y credenciales actualizados correctamente.");
            return "redirect:/view/empleados";

        }catch(DataIntegrityViolationException e){
            return volverFormularioConError(model,empleado,usuarioTemporal(usuarioExistente,username,idRol,estadoUsuario),
                    "No se pudo guardar. Revisa que documento, usuario o correo no estén repetidos.");
        }
    }

    @PostMapping("/view/empleados/delete/{id}")
    public String delete(@PathVariable Long id,RedirectAttributes ra){
        if(!empleadosRepository.existsById(id)){
            ra.addFlashAttribute("error","El empleado no existe.");
            return "redirect:/view/empleados";
        }
        try{
            empleadosRepository.deleteById(id);
            empleadosRepository.flush();
            ra.addFlashAttribute("mensaje","Empleado eliminado. Su usuario de acceso también fue eliminado.");
        }catch(DataIntegrityViolationException e){
            ra.addFlashAttribute("error",
                    "No se puede eliminar este empleado porque tiene ventas, compras u otros registros asociados. Cámbialo a INACTIVO y guarda los cambios.");
        }
        return "redirect:/view/empleados";
    }

    private String volverFormularioConError(Model model,Empleados empleado,Usuarios usuario,String mensaje){
        model.addAttribute("empleado",empleado);
        model.addAttribute("usuario",usuario);
        model.addAttribute("error",mensaje);
        cargarListas(model);
        return "empleados/empleadosForm";
    }

    private Usuarios usuarioTemporal(Usuarios existente,String username,Integer idRol,String estadoUsuario){
        Usuarios usuario=existente==null?new Usuarios():existente;
        usuario.setUsername(username);
        if(idRol!=null) rolesRepository.findById(idRol).ifPresent(usuario::setRol);
        try{
            usuario.setEstado(Usuarios.EstadoUsuario.valueOf(estadoUsuario.toUpperCase()));
        }catch(Exception ignored){
            usuario.setEstado(Usuarios.EstadoUsuario.ACTIVO);
        }
        return usuario;
    }

    private void cargarListas(Model model){
        model.addAttribute("sucursales",sucursalesRepository.findAll());
        model.addAttribute("roles",rolesRepository.findAll());
    }

    private void normalizarEmpleado(Empleados empleado){
        if(empleado.getNombre_completo()!=null) empleado.setNombre_completo(empleado.getNombre_completo().trim());
        if(empleado.getNumero_documento()!=null) empleado.setNumero_documento(empleado.getNumero_documento().trim());
        if(empleado.getCorreo()!=null) empleado.setCorreo(empleado.getCorreo().trim().toLowerCase());
        if(empleado.getTelefono()!=null) empleado.setTelefono(empleado.getTelefono().trim());
        if(empleado.getCargo()!=null) empleado.setCargo(empleado.getCargo().trim());
        if(empleado.getEstado()==null || empleado.getEstado().isBlank()) empleado.setEstado("ACTIVO");
    }
}
