package com.pillone.pillone.controller;

import com.pillone.pillone.model.Domicilios;
import com.pillone.pillone.repository.DomiciliosRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/domicilios")
public class DomiciliosController {

    private final DomiciliosRepository domiciliosRepository;

    private final Path uploadDir=Paths
            .get("uploads","domicilios")
            .toAbsolutePath()
            .normalize();

    private static final List<String> ESTADOS_VALIDOS=List.of(
            "PENDIENTE",
            "EN_PREPARACION",
            "EN_CAMINO",
            "ENTREGADO",
            "CANCELADO"
    );

    public DomiciliosController(DomiciliosRepository domiciliosRepository){
        this.domiciliosRepository=domiciliosRepository;
    }

    @GetMapping
    public List<Domicilios> listar(){
        return domiciliosRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Domicilios> obtener(@PathVariable Long id){
        return domiciliosRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    @Transactional
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado){

        Optional<Domicilios> opt=
                domiciliosRepository.findById(id);

        if(opt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        String nuevoEstado=
                estado==null
                        ? ""
                        : estado.trim().toUpperCase();

        if(!ESTADOS_VALIDOS.contains(nuevoEstado)){
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error",
                            "Estado inválido"
                    )
            );
        }

        Domicilios domicilio=
                opt.get();

        domicilio.setEstado(
                nuevoEstado
        );

        if(
                nuevoEstado.equals("EN_CAMINO") &&
                        domicilio.getFechaHoraSalida()==null
        ){
            domicilio.setFechaHoraSalida(
                    LocalDateTime.now()
            );
        }

        if(
                nuevoEstado.equals("ENTREGADO") &&
                        domicilio.getFechaHoraEntrega()==null
        ){
            domicilio.setFechaHoraEntrega(
                    LocalDateTime.now()
            );
        }

        domiciliosRepository.save(
                domicilio
        );

        return ResponseEntity.ok(
                domicilio
        );
    }

    @PostMapping(
            value="/{id}/entregar",
            consumes=MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Transactional
    public ResponseEntity<?> cerrarEntrega(
            @PathVariable Long id,
            @RequestParam(defaultValue="false") boolean pagado,
            @RequestParam(required=false) String observaciones,
            @RequestPart(required=false) MultipartFile foto){

        Optional<Domicilios> opt=
                domiciliosRepository.findById(id);

        if(opt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Domicilios domicilio=
                opt.get();

        domicilio.setEstado(
                "ENTREGADO"
        );

        domicilio.setFechaHoraEntrega(
                LocalDateTime.now()
        );

        domicilio.setPagado(
                pagado
        );

        if(pagado){
            domicilio.setFechaPago(
                    LocalDateTime.now()
            );
        }else{
            domicilio.setFechaPago(
                    null
            );
        }

        if(
                observaciones!=null &&
                        !observaciones.isBlank()
        ){
            domicilio.setObservacionesEntrega(
                    observaciones.trim()
            );
        }else{
            domicilio.setObservacionesEntrega(
                    null
            );
        }

        if(
                foto!=null &&
                        !foto.isEmpty()
        ){

            String contentType=
                    foto.getContentType();

            List<String> permitidos=
                    List.of(
                            "image/jpeg",
                            "image/png",
                            "image/webp"
                    );

            if(
                    contentType==null ||
                            !permitidos.contains(contentType)
            ){
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "error",
                                "La evidencia debe ser JPG, PNG o WEBP"
                        )
                );
            }

            if(
                    foto.getSize() >
                            5*1024*1024
            ){
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "error",
                                "La foto no puede superar 5 MB"
                        )
                );
            }

            try{

                Files.createDirectories(
                        uploadDir
                );

                String original=
                        foto.getOriginalFilename();

                String extension=
                        ".jpg";

                if(
                        original!=null &&
                                original.contains(".")
                ){
                    extension=
                            original.substring(
                                    original.lastIndexOf(".")
                            ).toLowerCase();
                }

                String nombre=
                        "domicilio_"+
                                id+"_"+
                                System.currentTimeMillis()+
                                extension;

                Path destino=
                        uploadDir
                                .resolve(nombre)
                                .normalize();

                if(
                        !destino.startsWith(
                                uploadDir
                        )
                ){
                    return ResponseEntity
                            .badRequest()
                            .body(
                                    Map.of(
                                            "error",
                                            "Nombre de archivo inválido"
                                    )
                            );
                }

                Files.copy(
                        foto.getInputStream(),
                        destino,
                        StandardCopyOption.REPLACE_EXISTING
                );

                domicilio.setEvidenciaEntrega(
                        "/uploads/domicilios/"+
                                nombre
                );

            }catch(IOException e){

                return ResponseEntity
                        .status(
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                        .body(
                                Map.of(
                                        "error",
                                        "No fue posible guardar la evidencia"
                                )
                        );
            }
        }

        domiciliosRepository.save(
                domicilio
        );

        return ResponseEntity.ok(
                Map.of(
                        "mensaje",
                        "Entrega cerrada correctamente",
                        "domicilio",
                        domicilio
                )
        );
    }
}