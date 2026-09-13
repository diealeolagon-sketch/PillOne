package com.pillone.pillone.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "domicilios")
@Getter
@Setter
@NoArgsConstructor
public class Domicilios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_domicilio")
    private Long idDomicilio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false, unique = true)
    private Ventas venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Clientes cliente;

    @Column(name = "direccion_entrega", nullable = false, length = 200)
    private String direccionEntrega;

    @Column(name = "telefono_contacto", nullable = false, length = 15)
    private String telefonoContacto;

    @Column(name = "nombre_domiciliario", length = 100)
    private String nombreDomiciliario;

    @Column(name = "costo_domicilio", precision = 10, scale = 2)
    private BigDecimal costoDomicilio = BigDecimal.ZERO;

    @Column(name = "pagado", nullable = false)
    private Boolean pagado = false;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "estado", length = 30)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    @Column(name = "fecha_hora_entrega")
    private LocalDateTime fechaHoraEntrega;

    @Column(name = "evidencia_entrega", length = 255)
    private String evidenciaEntrega;

    @Column(name = "observaciones_entrega", length = 500)
    private String observacionesEntrega;
}