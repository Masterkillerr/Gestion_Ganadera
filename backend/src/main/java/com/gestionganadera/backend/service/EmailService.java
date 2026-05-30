package com.gestionganadera.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

  private final RestTemplate http = new RestTemplate();
  private final String apiKey;
  private final String fromEmail;

  public EmailService(@Value("${RESEND_API_KEY:}") String apiKey,
                      @Value("${app.mail.from:}") String fromEmail) {
    this.apiKey = apiKey;
    this.fromEmail = fromEmail;
  }

  public void sendWelcomeEmail(String toEmail, String nombre) {
    if (apiKey == null || apiKey.isBlank() || fromEmail == null || fromEmail.isBlank()) return;

    String body = """
      {
        "from": "%s",
        "to": ["%s"],
        "subject": "Bienvenido a GestGan",
        "text": "Hola %s!\\n\\nGracias por registrarte en GestGan, tu sistema de gestion ganadera.\\nYa podes comenzar a registrar tu hato, lotes, producciones y mucho mas.\\n\\nSaludos,\\nEquipo GestGan"
      }
      """.formatted(fromEmail, toEmail, nombre);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);
    HttpEntity<String> req = new HttpEntity<>(body, headers);

    try {
      http.postForEntity("https://api.resend.com/emails", req, String.class);
    } catch (Exception ignored) { }
  }
}
