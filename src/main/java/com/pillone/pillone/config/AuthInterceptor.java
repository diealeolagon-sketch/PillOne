package com.pillone.pillone.config;

import com.pillone.pillone.model.Permisos;
import com.pillone.pillone.model.Roles;
import com.pillone.pillone.repository.RolesRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final RolesRepository rolesRepository;

    public AuthInterceptor(RolesRepository rolesRepository){
        this.rolesRepository=rolesRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception{
        String path=request.getRequestURI().substring(request.getContextPath().length());

        if(esPublica(path)) return true;

        HttpSession session=request.getSession(false);
        if(session==null || session.getAttribute("idUsuario")==null){
            response.sendRedirect(request.getContextPath()+"/login");
            return false;
        }

        Integer idRol=(Integer)session.getAttribute("idRol");
        if(idRol==null){
            response.sendRedirect(request.getContextPath()+"/login");
            return false;
        }

        Roles rol=rolesRepository.findById(idRol).orElse(null);
        if(rol==null){
            session.invalidate();
            response.sendRedirect(request.getContextPath()+"/login");
            return false;
        }

        Set<String> permisos=new LinkedHashSet<>();
        if(rol.getPermisos()!=null){
            for(Permisos p:rol.getPermisos()){
                if(Boolean.TRUE.equals(p.getActivo())) permisos.add(p.getCodigo());
            }
        }
        session.setAttribute("permisos",permisos);
        session.setAttribute("nombreRol",rol.getNombre());

        String requerido=permisoRequerido(request.getMethod(),path);
        if(requerido!=null && !permisos.contains(requerido)){
            response.sendRedirect(request.getContextPath()+"/acceso-denegado");
            return false;
        }

        return true;
    }

    private boolean esPublica(String path){
        return path.equals("/") ||
               path.equals("/login") ||
               path.equals("/recuperar-contrasena") ||
               path.equals("/restablecer-contrasena") ||
               path.equals("/acceso-denegado") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/img/") ||
               path.startsWith("/uploads/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/error");
    }

    private String permisoRequerido(String method,String path){
        if(path.startsWith("/view/dashboard")) return "DASHBOARD_VER";

        if(path.startsWith("/view/roles/form") || path.startsWith("/view/roles/edit") ||
           path.startsWith("/view/roles/save") || path.startsWith("/view/roles/delete")) return "ROLES_GESTIONAR";
        if(path.startsWith("/view/roles")) return "ROLES_VER";

        if(path.startsWith("/view/empleados/form") || path.startsWith("/view/empleados/edit") ||
           path.startsWith("/view/empleados/save") || path.startsWith("/view/empleados/delete")) return "EMPLEADOS_GESTIONAR";
        if(path.startsWith("/view/empleados")) return "EMPLEADOS_VER";

        if(path.startsWith("/view/productos/form")) return "PRODUCTOS_CREAR";
        if(path.startsWith("/view/productos/edit")) return "PRODUCTOS_EDITAR";
        if(path.startsWith("/view/productos/delete")) return "PRODUCTOS_ELIMINAR";
        if(path.startsWith("/view/productos")) return "PRODUCTOS_VER";

        if(path.startsWith("/view/categorias/form") || path.startsWith("/view/categorias/edit") ||
           path.startsWith("/view/categorias/save") || path.startsWith("/view/categorias/delete")) return "CATEGORIAS_GESTIONAR";
        if(path.startsWith("/view/categorias")) return "CATEGORIAS_VER";

        if(path.startsWith("/view/lotes/form")) return "LOTES_CREAR";
        if(path.contains("/retirar") || path.contains("/reactivar")) return "LOTES_RETIRAR";
        if(path.startsWith("/view/lotes")) return "LOTES_VER";
        if(path.startsWith("/view/alertas")) return "ALERTAS_VER";

        if(path.startsWith("/view/ventas/form")) return "VENTAS_CREAR";
        if(path.startsWith("/view/ventas")) return "VENTAS_VER";

        if(path.startsWith("/view/formulas/form")) return "FORMULAS_CREAR";
        if(path.startsWith("/view/formulas/edit")) return "FORMULAS_EDITAR";
        if(path.startsWith("/view/formulas")) return "FORMULAS_VER";

        if(path.startsWith("/view/proveedores/form")) return "PROVEEDORES_CREAR";
        if(path.startsWith("/view/proveedores/edit")) return "PROVEEDORES_EDITAR";
        if(path.startsWith("/view/proveedores/delete")) return "PROVEEDORES_INACTIVAR";
        if(path.startsWith("/view/proveedores")) return "PROVEEDORES_VER";

        if(path.startsWith("/view/compras/recibir")) return "COMPRAS_RECIBIR";
        if(path.startsWith("/view/compras/form")) return "COMPRAS_CREAR";
        if(path.startsWith("/view/compras/edit")) return "COMPRAS_EDITAR";
        if(path.startsWith("/view/compras/cancel")) return "COMPRAS_CANCELAR";
        if(path.startsWith("/view/compras")) return "COMPRAS_VER";

        if(path.startsWith("/view/clientes/form")) return "CLIENTES_CREAR";
        if(path.startsWith("/view/clientes/edit")) return "CLIENTES_EDITAR";
        if(path.startsWith("/view/clientes")) return "CLIENTES_VER";

        if(path.startsWith("/view/domicilios/gestionar") ||
           path.startsWith("/view/domicilios/avanzar") ||
           path.startsWith("/view/domicilios/entregar") ||
           path.startsWith("/view/domicilios/cancelar")) return "DOMICILIOS_GESTIONAR";
        if(path.startsWith("/view/domicilios")) return "DOMICILIOS_VER";

        if(path.startsWith("/view/devoluciones/domicilio")) return "DEVOLUCIONES_GESTIONAR";
        if(path.startsWith("/view/devoluciones")) return "DEVOLUCIONES_VER";
        if(path.startsWith("/view/reportes")) return "REPORTES_VER";

        if(path.startsWith("/view/sucursales/form") || path.startsWith("/view/sucursales/edit") ||
           path.startsWith("/view/sucursales/save") || path.startsWith("/view/sucursales/delete")) return "SUCURSALES_GESTIONAR";
        if(path.startsWith("/view/sucursales")) return "SUCURSALES_VER";

        if(path.startsWith("/view/configuracion")) return "CONFIGURACION_VER";

        if(path.startsWith("/api/roles")) return "ROLES_VER";

        return null;
    }
}
