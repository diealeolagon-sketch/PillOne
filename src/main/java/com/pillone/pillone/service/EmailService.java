package com.pillone.pillone.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.nombre:PillOne}")
    private String nombreApp;

    public EmailService(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }

    public void enviarRecuperacion(String destinatario,String nombre,String enlace){
        SimpleMailMessage mensaje=new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject(nombreApp+" - Recuperación de contraseña");
        mensaje.setText(
                "Hola "+(nombre==null?"":nombre)+",\n\n"+
                "Recibimos una solicitud para restablecer la contraseña de tu cuenta en "+nombreApp+".\n\n"+
                "Abre este enlace para crear una nueva contraseña:\n"+enlace+"\n\n"+
                "El enlace vence en 30 minutos y solo puede usarse una vez.\n\n"+
                "Si no solicitaste este cambio, puedes ignorar este correo.\n\n"+
                nombreApp
        );
        mailSender.send(mensaje);
    }
}
