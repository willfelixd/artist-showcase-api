package com.artistshowcase.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {

    @NotBlank(message = "Nome do solicitante é obrigatório")
    private String requesterName;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String requesterEmail;

    @NotBlank(message = "Telefone é obrigatório")
    private String requesterPhone;

    @NotBlank(message = "Nome do evento é obrigatório")
    private String eventName;

    @NotNull(message = "Data do evento é obrigatória")
    @Future(message = "A data do evento deve ser no futuro")
    private LocalDate eventDate;

    @NotNull(message = "Horário de início é obrigatório")
    private LocalTime startTime;

    @NotNull(message = "Horário de fim é obrigatório")
    private LocalTime endTime;

    @NotBlank(message = "Local do evento é obrigatório")
    private String location;

    private String notes;
}