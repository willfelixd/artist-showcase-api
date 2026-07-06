package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.ContactRequestDTO;
import com.artistshowcase.api.dto.ContactResponseDTO;
import com.artistshowcase.api.exception.RateLimitException;
import com.artistshowcase.api.model.ContactMessage;
import com.artistshowcase.api.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;

    public ContactService(
            ContactRepository contactRepository,
            EmailService emailService,
            RateLimiterService rateLimiterService
    ) {
        this.contactRepository = contactRepository;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
    }

    public ContactResponseDTO send(ContactRequestDTO dto, String senderIp) {
        // 1. Verificar rate limit ANTES de qualquer operação
        if (!rateLimiterService.isAllowed(senderIp)) {
            log.warn("Rate limit atingido para IP: {}", senderIp);
            throw new RateLimitException(
                    "Muitas mensagens enviadas. Tente novamente em 1 hora."
            );
        }

        // 2. Tentar enviar o e-mail
        boolean emailSent = emailService.sendContactEmail(dto);

        // 3. Persistir no banco independente do resultado do e-mail
        ContactMessage contact = new ContactMessage();
        contact.setSenderName(dto.getSenderName());
        contact.setSenderEmail(dto.getSenderEmail());
        contact.setSenderPhone(dto.getSenderPhone());
        contact.setSubject(dto.getSubject());
        contact.setMessage(dto.getMessage());
        contact.setSenderIp(senderIp);
        contact.setEmailSent(emailSent);

        ContactMessage saved = contactRepository.save(contact);
        log.info("Mensagem de contato salva. ID: {}, emailSent: {}", saved.getId(), emailSent);

        return toResponseDTO(saved);
    }

    public Page<ContactResponseDTO> findAll(int page, int size) {
        return contactRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::toResponseDTO);
    }

    public Page<ContactResponseDTO> findFailedEmails(int page, int size) {
        return contactRepository
                .findByEmailSentFalseOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::toResponseDTO);
    }

    public ContactResponseDTO findById(Long id) {
        return contactRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new NoSuchElementException("Mensagem não encontrada: " + id));
    }

    private ContactResponseDTO toResponseDTO(ContactMessage msg) {
        return new ContactResponseDTO(
                msg.getId(),
                msg.getSenderName(),
                msg.getSenderEmail(),
                msg.getSenderPhone(),
                msg.getSubject(),
                msg.getMessage(),
                msg.isEmailSent(),
                msg.getCreatedAt()
        );
    }
}