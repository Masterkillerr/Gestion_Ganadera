package com.gestionganadera.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${app.mail.from:}")
  private String fromAddress;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendWelcomeEmail(String toEmail, String nombre) {
    if (fromAddress == null || fromAddress.isBlank()) return;

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(fromAddress);
    msg.setTo(toEmail);
    msg.setSubject("Bienvenido a GestGan");
    msg.setText(
      "Hola " + nombre + "!\n\n" +
      "Gracias por registrarte en GestGan, tu sistema de gestión ganadera.\n" +
      "Ya podés comenzar a registrar tu hato, lotes, producciones y mucho más.\n\n" +
      "Saludos,\nEquipo GestGan"
    );
    mailSender.send(msg);
  }
}
