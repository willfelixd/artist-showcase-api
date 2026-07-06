package com.artistshowcase.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String senderName;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String senderEmail;

    private String senderPhone;

    @NotBlank(message = "Assunto é obrigatório")
    @Size(min = 3, max = 150, message = "Assunto deve ter entre 3 e 150 caracteres")
    private String subject;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(min = 10, max = 2000, message = "Mensagem deve ter entre 10 e 2000 caracteres")
    private String message;
}