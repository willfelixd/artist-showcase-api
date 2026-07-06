package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.ContactRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.to}")
    private String mailTo;

    @Value("${app.mail.from}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendContactEmail(ContactRequestDTO dto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(mailTo);
            message.setReplyTo(dto.getSenderEmail());
            message.setSubject("[Isa Cantora] " + dto.getSubject());
            message.setText(buildEmailBody(dto));

            mailSender.send(message);
            log.info("E-mail de contato enviado de: {}", dto.getSenderEmail());
            return true;

        } catch (MailException e) {
            log.error("Falha ao enviar e-mail de contato: {}", e.getMessage());
            return false;
        }
    }

    private String buildEmailBody(ContactRequestDTO dto) {
        return """
                Nova mensagem de contato recebida:
                
                Nome: %s
                Email: %s
                Telefone: %s
                
                Assunto: %s
                
                Mensagem:
                %s
                """.formatted(
                dto.getSenderName(),
                dto.getSenderEmail(),
                dto.getSenderPhone() != null ? dto.getSenderPhone() : "Não informado",
                dto.getSubject(),
                dto.getMessage()
        );
    }
}