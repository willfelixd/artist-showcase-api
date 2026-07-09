package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.BookingRequestDTO;
import com.artistshowcase.api.dto.BookingResponseDTO;
import com.artistshowcase.api.dto.BookingStatusUpdateDTO;
import com.artistshowcase.api.model.enums.BookingStatus;
import com.artistshowcase.api.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Agenda", description = "Gerenciamento de agendamentos de shows")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Solicitar agendamento",
            description = "Cria uma solicitação de show — valida conflitos de horário — endpoint público"
    )
    public BookingResponseDTO create(@Valid @RequestBody BookingRequestDTO dto) {
        return bookingService.create(dto);
    }

    @GetMapping("/unavailable-dates")
    @Operation(
            summary = "Datas indisponíveis",
            description = "Retorna lista de datas com shows confirmados — endpoint público"
    )
    public List<LocalDate> getUnavailableDates() {
        return bookingService.getUnavailableDates();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar agendamento por ID",
            description = "Retorna um agendamento específico — endpoint público"
    )
    public BookingResponseDTO findById(
            @Parameter(description = "ID do agendamento")
            @PathVariable Long id
    ) {
        return bookingService.findById(id);
    }

    @GetMapping
    @Operation(
            summary = "Listar agendamentos",
            description = "Retorna lista paginada de todos os agendamentos — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public Page<BookingResponseDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookingService.findAll(page, size);
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Filtrar por status",
            description = "Retorna agendamentos filtrados por status — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public Page<BookingResponseDTO> findByStatus(
            @Parameter(description = "Status do agendamento: PENDING, CONFIRMED ou CANCELLED")
            @PathVariable BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookingService.findByStatus(status, page, size);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Atualizar status",
            description = "Confirma ou cancela um agendamento — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public BookingResponseDTO updateStatus(
            @Parameter(description = "ID do agendamento")
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateDTO dto
    ) {
        return bookingService.updateStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar agendamento",
            description = "Remove um agendamento — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public void delete(
            @Parameter(description = "ID do agendamento")
            @PathVariable Long id
    ) {
        bookingService.delete(id);
    }
}