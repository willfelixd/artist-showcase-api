package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.BookingRequestDTO;
import com.artistshowcase.api.dto.BookingResponseDTO;
import com.artistshowcase.api.dto.BookingStatusUpdateDTO;
import com.artistshowcase.api.model.enums.BookingStatus;
import com.artistshowcase.api.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Público: solicitar agendamento
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO create(@Valid @RequestBody BookingRequestDTO dto) {
        return bookingService.create(dto);
    }

    // Público: datas indisponíveis para o calendário
    @GetMapping("/unavailable-dates")
    public List<LocalDate> getUnavailableDates() {
        return bookingService.getUnavailableDates();
    }

    // Público: buscar agendamento por ID (para confirmação)
    @GetMapping("/{id}")
    public BookingResponseDTO findById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    // Admin: listar todos
    @GetMapping
    public Page<BookingResponseDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookingService.findAll(page, size);
    }

    // Admin: filtrar por status
    @GetMapping("/status/{status}")
    public Page<BookingResponseDTO> findByStatus(
            @PathVariable BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookingService.findByStatus(status, page, size);
    }

    // Admin: atualizar status
    @PatchMapping("/{id}/status")
    public BookingResponseDTO updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdateDTO dto
    ) {
        return bookingService.updateStatus(id, dto);
    }

    // Admin: deletar
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookingService.delete(id);
    }
}