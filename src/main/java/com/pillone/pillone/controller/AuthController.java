package com.pillone.pillone.controller;

import com.pillone.pillone.model.Empleados;
import com.pillone.pillone.model.PasswordResetToken;
import com.pillone.pillone.model.Permisos;
import com.pillone.pillone.model.Usuarios;
import com.pillone.pillone.repository.EmpleadosRepository;
import com.pillone.pillone.repository.PasswordResetTokenRepository;
import com.pillone.pillone.repository.UsuariosRepository;
import com.pillone.pillone.service.EmailService;
import com.pillone.pillone.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {

    private final UsuariosRepository usuariosRepository;
    private final EmpleadosRepository empleadosRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final SecureRandom secureRandom=new SecureRandom();

    public AuthController(
            UsuariosRepository usuariosRepository,
            EmpleadosRepository empleadosRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordService passwordService,
            EmailService emailService
    ){
        this.usuariosRepository=usuariosRepository;
        this.empleadosRepository=empleadosRepository;
        this.tokenRepository=tokenRepository;
        this.passwordService=passwordService;
        this.emailService=emailService;
    }

    @GetMapping("/")
    public String inicio(HttpSession session){
        if(session.getAttribute("idUsuario")!=null){
            return "redirect:/view/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpSession session){
        if(session.getAttribute("idUsuario")!=null){
            return "redirect:/view/dashboard";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    @Transactional(readOnly=true)
    public String loginPost(
            @RequestParam("login") String login,
            @RequestParam("password") String password,
            HttpServletRequest request,
            Model model
    ){
        String identificador=login==null?"":login.trim();

        if(identificador.isBlank() || password==null || password.isBlank()){
            model.addAttribute("error","Ingresa tu usuario/correo y contraseña.");
            model.addAttribute("login",identificador);
            return "auth/login";
        }

        Optional<Usuarios> encontrado=
                usuariosRepository.buscarParaLogin(identificador);

        if(encontrado.isEmpty()){
            model.addAttribute(
                    "error",
                    "Usuario/correo o contraseña incorrectos."
            );
            model.addAttribute("login",identificador);
            return "auth/login";
        }

        Usuarios usuario=encontrado.get();

        if(usuario.getEstado()==null){
            model.addAttribute(
                    "error",
                    "La cuenta no tiene un estado válido."
            );
            return "auth/login";
        }

        if(usuario.getEstado()!=Usuarios.EstadoUsuario.ACTIVO){

            if(usuario.getEstado()==Usuarios.EstadoUsuario.BLOQUEADO){
                model.addAttribute(
                        "error",
                        "Tu cuenta está bloqueada. Contacta al administrador."
                );
            }else{
                model.addAttribute(
                        "error",
                        "Tu cuenta está inactiva. Contacta al administrador."
                );
            }

            model.addAttribute("login",identificador);
            return "auth/login";
        }

        Empleados empleado=
                empleadosRepository
                        .findById(usuario.getIdEmpleado())
                        .orElse(null);

        if(empleado==null){
            model.addAttribute(
                    "error",
                    "No se encontró el empleado asociado a esta cuenta."
            );
            return "auth/login";
        }

        if(
                empleado.getEstado()==null ||
                        !"ACTIVO".equalsIgnoreCase(
                                empleado.getEstado().toString()
                        )
        ){
            model.addAttribute(
                    "error",
                    "El empleado asociado está inactivo."
            );
            model.addAttribute("login",identificador);
            return "auth/login";
        }

        if(
                usuario.getPasswordHash()==null ||
                        usuario.getPasswordHash().isBlank()
        ){
            model.addAttribute(
                    "error",
                    "Esta cuenta no tiene una contraseña configurada."
            );
            return "auth/login";
        }

        if(!passwordService.esFormatoActual(usuario.getPasswordHash())){
            model.addAttribute(
                    "error",
                    "Esta cuenta tiene una contraseña antigua. Usa Recuperar contraseña."
            );
            model.addAttribute("login",identificador);
            return "auth/login";
        }

        if(!passwordService.matches(password,usuario.getPasswordHash())){
            model.addAttribute(
                    "error",
                    "Usuario/correo o contraseña incorrectos."
            );
            model.addAttribute("login",identificador);
            return "auth/login";
        }

        if(usuario.getRol()==null){
            model.addAttribute(
                    "error",
                    "La cuenta no tiene un rol asignado."
            );
            return "auth/login";
        }

        Set<String> permisos=new LinkedHashSet<>();

        if(usuario.getRol().getPermisos()!=null){

            for(Permisos permiso:usuario.getRol().getPermisos()){

                if(
                        permiso!=null &&
                                Boolean.TRUE.equals(permiso.getActivo()) &&
                                permiso.getCodigo()!=null
                ){
                    permisos.add(permiso.getCodigo());
                }
            }
        }

        HttpSession sesionAnterior=request.getSession(false);

        if(sesionAnterior!=null){
            sesionAnterior.invalidate();
        }

        HttpSession session=request.getSession(true);

        session.setAttribute(
                "idUsuario",
                usuario.getIdUsuario()
        );

        session.setAttribute(
                "idEmpleado",
                usuario.getIdEmpleado()
        );

        session.setAttribute(
                "username",
                usuario.getUsername()
        );

        session.setAttribute(
                "nombreEmpleado",
                empleado.getNombre_completo()
        );

        session.setAttribute(
                "correoEmpleado",
                empleado.getCorreo()
        );

        session.setAttribute(
                "idRol",
                usuario.getRol().getIdRol()
        );

        session.setAttribute(
                "nombreRol",
                usuario.getRol().getNombre()
        );

        session.setAttribute(
                "permisos",
                permisos
        );

        return "redirect:/view/dashboard";
    }

    @PostMapping("/logout")
    public String logout(
            HttpServletRequest request
    ){
        HttpSession session=request.getSession(false);

        if(session!=null){
            session.invalidate();
        }

        return "redirect:/login?logout";
    }

    @GetMapping("/recuperar-contrasena")
    public String recuperar(){
        return "auth/recuperarContrasena";
    }

    @PostMapping("/recuperar-contrasena")
    @Transactional
    public String recuperarPost(
            @RequestParam("identificador") String identificador,
            HttpServletRequest request,
            Model model
    ){
        String valor=
                identificador==null
                        ?""
                        :identificador.trim();

        if(valor.isBlank()){
            model.addAttribute(
                    "mensaje",
                    "Si la cuenta existe y tiene un correo registrado, enviaremos las instrucciones."
            );

            return "auth/recuperarContrasena";
        }

        Optional<Usuarios> usuarioOpt=
                usuariosRepository.buscarParaLogin(valor);

        if(usuarioOpt.isPresent()){

            Usuarios usuario=usuarioOpt.get();

            Empleados empleado=
                    empleadosRepository
                            .findById(usuario.getIdEmpleado())
                            .orElse(null);

            if(
                    empleado!=null &&
                            empleado.getCorreo()!=null &&
                            !empleado.getCorreo().isBlank()
            ){

                for(
                        PasswordResetToken anterior:
                        tokenRepository
                                .findAllByUsuarioAndUsadoFalse(usuario)
                ){
                    anterior.setUsado(true);
                    tokenRepository.save(anterior);
                }

                String token=generarTokenSeguro();

                PasswordResetToken registro=
                        new PasswordResetToken();

                registro.setUsuario(usuario);
                registro.setTokenHash(
                        sha256(token)
                );
                registro.setFechaCreacion(
                        LocalDateTime.now()
                );
                registro.setFechaExpiracion(
                        LocalDateTime.now().plusMinutes(30)
                );
                registro.setUsado(false);

                tokenRepository.save(registro);

                String base=
                        request.getScheme()+
                                "://"+
                                request.getServerName()+
                                (
                                        request.getServerPort()==80 ||
                                                request.getServerPort()==443
                                                ?""
                                                :":"+request.getServerPort()
                                );

                String enlace=
                        base+
                                request.getContextPath()+
                                "/restablecer-contrasena?token="+
                                token;

                try{
                    emailService.enviarRecuperacion(
                            empleado.getCorreo(),
                            empleado.getNombre_completo(),
                            enlace
                    );
                }catch(Exception e){
                    System.err.println(
                            "No fue posible enviar el correo: "+
                                    e.getMessage()
                    );
                }
            }
        }

        model.addAttribute(
                "mensaje",
                "Si la cuenta existe y tiene un correo registrado, enviamos las instrucciones para restablecer la contraseña."
        );

        return "auth/recuperarContrasena";
    }

    @GetMapping("/restablecer-contrasena")
    public String restablecer(
            @RequestParam("token") String token,
            Model model
    ){
        if(token==null || token.isBlank()){
            model.addAttribute(
                    "tokenInvalido",
                    true
            );

            model.addAttribute(
                    "error",
                    "El enlace de recuperación no es válido."
            );

            return "auth/restablecerContrasena";
        }

        PasswordResetToken registro=
                tokenRepository
                        .findByTokenHash(
                                sha256(token)
                        )
                        .orElse(null);

        if(
                registro==null ||
                        !registro.esValido()
        ){
            model.addAttribute(
                    "tokenInvalido",
                    true
            );

            model.addAttribute(
                    "error",
                    "El enlace es inválido, ya fue usado o expiró."
            );

            return "auth/restablecerContrasena";
        }

        model.addAttribute(
                "token",
                token
        );

        return "auth/restablecerContrasena";
    }

    @PostMapping("/restablecer-contrasena")
    @Transactional
    public String restablecerPost(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmarPassword") String confirmarPassword,
            Model model
    ){
        PasswordResetToken registro=
                tokenRepository
                        .findByTokenHash(
                                sha256(token)
                        )
                        .orElse(null);

        if(
                registro==null ||
                        !registro.esValido()
        ){
            model.addAttribute(
                    "tokenInvalido",
                    true
            );

            model.addAttribute(
                    "error",
                    "El enlace es inválido, ya fue usado o expiró."
            );

            return "auth/restablecerContrasena";
        }

        if(
                password==null ||
                        password.length()<8
        ){
            model.addAttribute(
                    "token",
                    token
            );

            model.addAttribute(
                    "error",
                    "La nueva contraseña debe tener mínimo 8 caracteres."
            );

            return "auth/restablecerContrasena";
        }

        if(
                confirmarPassword==null ||
                        !password.equals(confirmarPassword)
        ){
            model.addAttribute(
                    "token",
                    token
            );

            model.addAttribute(
                    "error",
                    "Las contraseñas no coinciden."
            );

            return "auth/restablecerContrasena";
        }

        Usuarios usuario=
                registro.getUsuario();

        usuario.setPasswordHash(
                passwordService.hash(password)
        );

        if(
                usuario.getEstado()==
                        Usuarios.EstadoUsuario.BLOQUEADO
        ){
            usuario.setEstado(
                    Usuarios.EstadoUsuario.ACTIVO
            );
        }

        usuariosRepository.save(usuario);

        registro.setUsado(true);
        tokenRepository.save(registro);

        for(
                PasswordResetToken otro:
                tokenRepository
                        .findAllByUsuarioAndUsadoFalse(usuario)
        ){
            otro.setUsado(true);
            tokenRepository.save(otro);
        }

        return "redirect:/login?reset=ok";
    }

    private String generarTokenSeguro(){
        byte[] bytes=new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String sha256(String value){

        try{
            byte[] hash=
                    MessageDigest
                            .getInstance("SHA-256")
                            .digest(
                                    value.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            );

            StringBuilder sb=
                    new StringBuilder();

            for(byte b:hash){
                sb.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return sb.toString();

        }catch(Exception e){

            throw new IllegalStateException(
                    "No fue posible procesar el token.",
                    e
            );
        }
    }
}