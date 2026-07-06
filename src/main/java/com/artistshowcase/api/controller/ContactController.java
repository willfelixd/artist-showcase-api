package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.ContactRequestDTO;
import com.artistshowcase.api.dto.ContactResponseDTO;
import com.artistshowcase.api.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Público: enviar mensagem
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponseDTO send(
            @Valid @RequestBody ContactRequestDTO dto,
            HttpServletRequest request
    ) {
        String ip = getClientIp(request);
        return contactService.send(dto, ip);
    }

    // Admin: listar todas as mensagens
    @GetMapping
    public Page<ContactResponseDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return contactService.findAll(page, size);
    }

    // Admin: mensagens com falha no envio de e-mail
    @GetMapping("/failed")
    public Page<ContactResponseDTO> findFailedEmails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return contactService.findFailedEmails(page, size);
    }

    // Admin: detalhe de uma mensagem
    @GetMapping("/{id}")
    public ContactResponseDTO findById(@PathVariable Long id) {
        return contactService.findById(id);
    }

    // Extrai IP real considerando proxies (Nginx, Render, etc.)
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}