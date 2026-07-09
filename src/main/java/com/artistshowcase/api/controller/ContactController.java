package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.ContactRequestDTO;
import com.artistshowcase.api.dto.ContactResponseDTO;
import com.artistshowcase.api.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@Tag(name = "Contato", description = "Gerenciamento de mensagens de contato")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Enviar mensagem",
            description = "Envia mensagem de contato com rate limiting de 3 mensagens por hora por IP — endpoint público"
    )
    public ContactResponseDTO send(
            @Valid @RequestBody ContactRequestDTO dto,
            HttpServletRequest request
    ) {
        String ip = getClientIp(request);
        return contactService.send(dto, ip);
    }

    @GetMapping
    @Operation(
            summary = "Listar mensagens",
            description = "Retorna lista paginada de todas as mensagens — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public Page<ContactResponseDTO> findAll(
            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "20") int size
    ) {
        return contactService.findAll(page, size);
    }

    @GetMapping("/failed")
    @Operation(
            summary = "Mensagens com falha no e-mail",
            description = "Retorna mensagens onde o envio de e-mail falhou — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public Page<ContactResponseDTO> findFailedEmails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return contactService.findFailedEmails(page, size);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar mensagem por ID",
            description = "Retorna o detalhe de uma mensagem — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public ContactResponseDTO findById(
            @Parameter(description = "ID da mensagem")
            @PathVariable Long id
    ) {
        return contactService.findById(id);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}