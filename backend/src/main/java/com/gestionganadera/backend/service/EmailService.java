package com.gestionganadera.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(
            @Value("${MAIL_HOST:smtp.gmail.com}") String host,
            @Value("${MAIL_PORT:587}") int port,
            @Value("${MAIL_USERNAME:}") String username,
            @Value("${MAIL_PASSWORD:}") String password,
            @Value("${app.mail.from:}") String fromEmail) {

        this.fromEmail = fromEmail;

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            this.mailSender = null;
            return;
        }

        JavaMailSenderImpl impl = new JavaMailSenderImpl();
        impl.setHost(host);
        impl.setPort(port);
        impl.setUsername(username);
        impl.setPassword(password);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        impl.setJavaMailProperties(props);

        this.mailSender = impl;
    }

    public void sendWelcomeEmail(String toEmail, String nombre) {
        if (mailSender == null || fromEmail == null || fromEmail.isBlank()) return;

        String subject = "Bienvenido a GestGan";
        String text = String.format(
                "Hola %s!\n\n" +
                "Gracias por registrarte en GestGan, tu sistema de gestión ganadera.\n" +
                "Ya podés comenzar a registrar tu hato, lotes, producciones y mucho más.\n\n" +
                "Saludos,\nEquipo GestGan",
                nombre);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(text);

        try {
            mailSender.send(msg);
        } catch (Exception ignored) { }
    }
}
