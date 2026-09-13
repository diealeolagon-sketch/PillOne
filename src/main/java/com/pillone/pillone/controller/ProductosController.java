package com.pillone.pillone.controller;

import com.pillone.pillone.model.Productos;
import com.pillone.pillone.repository.LotesRepository;
import com.pillone.pillone.repository.ProductosRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {

    private final ProductosRepository productosRepository;
    private final LotesRepository lotesRepository;

    public ProductosController(
            ProductosRepository productosRepository,
            LotesRepository lotesRepository
    ){
        this.productosRepository=productosRepository;
        this.lotesRepository=lotesRepository;
    }

    @GetMapping
    public List<Productos> listar(){

        List<Productos> productos=
                productosRepository.findAll();

        productos.forEach(
                this::cargarStockCalculado
        );

        return productos;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Productos> obtener(
            @PathVariable Long id
    ){
        Productos producto=
                productosRepository
                        .findById(id)
                        .orElse(null);

        if(producto==null){
            return ResponseEntity.notFound().build();
        }

        cargarStockCalculado(producto);

        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Productos producto
    ){
        validarYCalcularEmpaque(producto);

        producto.setIdProducto(null);

        /*
         * El producto nace sin inventario.
         * El inventario se crea después mediante lotes.
         */
        producto.setStockTotal(0);

        if(
                producto.getEstado()==null ||
                        producto.getEstado().isBlank()
        ){
            producto.setEstado("ACTIVO");
        }

        return ResponseEntity.ok(
                productosRepository.save(producto)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Productos producto
    ){
        if(!productosRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        producto.setIdProducto(id);

        validarYCalcularEmpaque(producto);

        cargarStockCalculado(producto);

        return ResponseEntity.ok(
                productosRepository.save(producto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id
    ){
        if(!productosRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        if(
                !lotesRepository
                        .findByIdProductoOrderByFechaVencimientoAscIdLoteAsc(id)
                        .isEmpty()
        ){
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "No se puede eliminar porque tiene lotes asociados. Cambie el producto a INACTIVO."
                            )
                    );
        }

        productosRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Producto eliminado"
                )
        );
    }

    private void cargarStockCalculado(
            Productos producto
    ){
        Integer stock=
                lotesRepository
                        .sumarStockDisponible(
                                producto.getIdProducto(),
                                LocalDate.now()
                        );

        producto.setStockTotal(
                stock==null
                        ? 0
                        : stock
        );
    }

    private void validarYCalcularEmpaque(
            Productos producto
    ){
        if(
                producto.getSellosPorCaja()==null ||
                        producto.getSellosPorCaja()<=0
        ){
            producto.setSellosPorCaja(1);
        }

        if(
                producto.getUnidadesPorSello()==null ||
                        producto.getUnidadesPorSello()<=0
        ){
            producto.setUnidadesPorSello(1);
        }

        producto.setUnidadesPorEmpaque(
                producto.getSellosPorCaja() *
                        producto.getUnidadesPorSello()
        );
    }
}