package com.pillone.pillone.view;

import com.pillone.pillone.model.Lotes;
import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.CategoriasRepository;
import com.pillone.pillone.repository.LotesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import com.pillone.pillone.repository.ProveedoresRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductosView {

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private CategoriasRepository categoriasRepository;

    @Autowired
    private ProveedoresRepository proveedoresRepository;

    @Autowired
    private LotesRepository lotesRepository;

    @GetMapping("/view/productos")
    public String lista(Model model){

        List<Productos> productos=productosRepository.findAll();

        Map<Long,ResumenLotes> resumenLotes=new HashMap<>();

        LocalDate hoy=LocalDate.now();

        for(Productos producto:productos){

            List<Lotes> lotes=
                    lotesRepository
                            .findByIdProductoOrderByFechaVencimientoAscIdLoteAsc(
                                    producto.getIdProducto()
                            );

            int cantidadLotes=0;
            int stockDisponible=0;
            LocalDate proximoVencimiento=null;

            for(Lotes lote:lotes){

                if(lote.getCantidadActual()==null ||
                        lote.getCantidadActual()<=0){
                    continue;
                }

                if(lote.getFechaVencimiento()==null ||
                        lote.getFechaVencimiento().isBefore(hoy)){
                    continue;
                }

                String estado=lote.getEstado();

                if("RETIRADO".equalsIgnoreCase(estado) ||
                        "DEVUELTO".equalsIgnoreCase(estado) ||
                        "VENCIDO".equalsIgnoreCase(estado)){
                    continue;
                }

                cantidadLotes++;

                stockDisponible+=
                        lote.getCantidadActual();

                if(proximoVencimiento==null ||
                        lote.getFechaVencimiento()
                                .isBefore(proximoVencimiento)){

                    proximoVencimiento=
                            lote.getFechaVencimiento();
                }
            }

            producto.setStockTotal(stockDisponible);

            resumenLotes.put(
                    producto.getIdProducto(),
                    new ResumenLotes(
                            cantidadLotes,
                            stockDisponible,
                            proximoVencimiento
                    )
            );
        }

        model.addAttribute(
                "productos",
                productos
        );

        /*
         * ESTA ES LA LISTA QUE ALIMENTA EL SELECT
         * DE CATEGORÍAS EN productos.html
         */
        model.addAttribute(
                "categorias",
                categoriasRepository.findAll()
        );

        model.addAttribute(
                "proveedores",
                proveedoresRepository.findAll()
        );

        model.addAttribute(
                "resumenLotes",
                resumenLotes
        );

        return "productos/productos";
    }

    @GetMapping("/view/productos/form")
    public String form(Model model){

        Productos producto=
                new Productos();

        if(producto.getStockTotal()==null){
            producto.setStockTotal(0);
        }

        model.addAttribute(
                "producto",
                producto
        );

        cargarListas(model);

        return "productos/productosForm";
    }

    @PostMapping("/view/productos/save")
    public String save(
            @Valid @ModelAttribute("producto") Productos producto,
            BindingResult result,
            Model model,
            RedirectAttributes ra
    ){

        if(result.hasErrors()){

            model.addAttribute(
                    "error",
                    "Hay campos obligatorios vacíos o incorrectos."
            );

            cargarListas(model);

            return "productos/productosForm";
        }

        if(producto.getCodigoInterno()!=null){

            producto.setCodigoInterno(
                    producto.getCodigoInterno()
                            .trim()
            );
        }

        if(producto.getCodigoBarras()!=null){

            producto.setCodigoBarras(
                    producto.getCodigoBarras()
                            .trim()
            );
        }

        if(producto.getNombreComercial()!=null){

            producto.setNombreComercial(
                    producto.getNombreComercial()
                            .trim()
            );
        }

        /*
         * EL STOCK NO SE REGISTRA MANUALMENTE EN PRODUCTO.
         * LOS LOTES SON LA FUENTE REAL DEL INVENTARIO.
         */
        if(producto.getIdProducto()==null){

            producto.setStockTotal(0);

        }else{

            Productos existente=
                    productosRepository
                            .findById(
                                    producto.getIdProducto()
                            )
                            .orElse(null);

            if(existente!=null){

                producto.setStockTotal(
                        existente.getStockTotal()==null
                                ? 0
                                : existente.getStockTotal()
                );
            }
        }

        /*
         * VALIDACIÓN CÓDIGO INTERNO
         */
        if(producto.getCodigoInterno()!=null &&
                !producto.getCodigoInterno().isBlank()){

            boolean existeCodigo;

            if(producto.getIdProducto()==null){

                existeCodigo=
                        productosRepository
                                .existsByCodigoInternoIgnoreCase(
                                        producto.getCodigoInterno()
                                );

            }else{

                existeCodigo=
                        productosRepository
                                .existsByCodigoInternoIgnoreCaseAndIdProductoNot(
                                        producto.getCodigoInterno(),
                                        producto.getIdProducto()
                                );
            }

            if(existeCodigo){

                model.addAttribute(
                        "error",
                        "El código interno ya está registrado en otro producto."
                );

                cargarListas(model);

                return "productos/productosForm";
            }
        }

        /*
         * VALIDACIÓN CÓDIGO DE BARRAS
         */
        if(producto.getCodigoBarras()!=null &&
                !producto.getCodigoBarras().isBlank()){

            boolean existeBarras;

            if(producto.getIdProducto()==null){

                existeBarras=
                        productosRepository
                                .existsByCodigoBarrasIgnoreCase(
                                        producto.getCodigoBarras()
                                );

            }else{

                existeBarras=
                        productosRepository
                                .existsByCodigoBarrasIgnoreCaseAndIdProductoNot(
                                        producto.getCodigoBarras(),
                                        producto.getIdProducto()
                                );
            }

            if(existeBarras){

                model.addAttribute(
                        "error",
                        "El código de barras ya está registrado en otro producto."
                );

                cargarListas(model);

                return "productos/productosForm";
            }
        }

        try{

            productosRepository.save(
                    producto
            );

            ra.addFlashAttribute(
                    "mensaje",
                    "Producto guardado correctamente."
            );

        }catch(DataIntegrityViolationException e){

            model.addAttribute(
                    "error",
                    "No fue posible guardar el producto. Revisa los códigos y los datos registrados."
            );

            cargarListas(model);

            return "productos/productosForm";
        }

        return "redirect:/view/productos";
    }

    @GetMapping("/view/productos/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model,
            RedirectAttributes ra
    ){

        Productos producto=
                productosRepository
                        .findById(id)
                        .orElse(null);

        if(producto==null){

            ra.addFlashAttribute(
                    "error",
                    "El producto no existe."
            );

            return "redirect:/view/productos";
        }

        model.addAttribute(
                "producto",
                producto
        );

        cargarListas(model);

        return "productos/productosForm";
    }

    @PostMapping("/view/productos/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes ra
    ){

        if(!productosRepository.existsById(id)){

            ra.addFlashAttribute(
                    "error",
                    "El producto no existe."
            );

            return "redirect:/view/productos";
        }

        try{

            productosRepository.deleteById(id);
            productosRepository.flush();

            ra.addFlashAttribute(
                    "mensaje",
                    "Producto eliminado correctamente."
            );

        }catch(DataIntegrityViolationException e){

            ra.addFlashAttribute(
                    "error",
                    "No se puede eliminar este producto porque tiene lotes, ventas u otros registros asociados."
            );
        }

        return "redirect:/view/productos";
    }

    private void cargarListas(Model model){

        model.addAttribute(
                "categorias",
                categoriasRepository.findAll()
        );

        model.addAttribute(
                "proveedores",
                proveedoresRepository.findAll()
        );
    }

    public static class ResumenLotes {

        private final int cantidadLotes;
        private final int stockDisponible;
        private final LocalDate proximoVencimiento;

        public ResumenLotes(
                int cantidadLotes,
                int stockDisponible,
                LocalDate proximoVencimiento
        ){
            this.cantidadLotes=cantidadLotes;
            this.stockDisponible=stockDisponible;
            this.proximoVencimiento=proximoVencimiento;
        }

        public int getCantidadLotes(){
            return cantidadLotes;
        }

        public int getStockDisponible(){
            return stockDisponible;
        }

        public LocalDate getProximoVencimiento(){
            return proximoVencimiento;
        }
    }
}