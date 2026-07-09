package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.BookingRequestDTO;
import com.artistshowcase.api.exception.BookingConflictException;
import com.artistshowcase.api.model.Booking;
import com.artistshowcase.api.model.enums.BookingStatus;
import com.artistshowcase.api.repository.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDTO buildRequest(
            LocalDate date,
            LocalTime start,
            LocalTime end
    ) {
        return new BookingRequestDTO(
                "João Silva", "joao@email.com", "11999999999",
                "Casamento", date, start, end,
                "Salão Festivo", null
        );
    }

    @Test
    @DisplayName("Deve rejeitar agendamento com horário de fim antes do início")
    void create_invalidTimes() {
        BookingRequestDTO dto = buildRequest(
                LocalDate.now().plusDays(10),
                LocalTime.of(20, 0),
                LocalTime.of(18, 0) // fim antes do início
        );

        assertThatThrownBy(() -> bookingService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fim deve ser após");
    }

    @Test
    @DisplayName("Deve rejeitar agendamento com conflito de horário")
    void create_conflictingBooking() {
        BookingRequestDTO dto = buildRequest(
                LocalDate.now().plusDays(10),
                LocalTime.of(19, 0),
                LocalTime.of(23, 0)
        );

        when(bookingRepository.hasConflict(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.create(dto))
                .isInstanceOf(BookingConflictException.class);
    }

    @Test
    @DisplayName("Deve criar agendamento quando não há conflito")
    void create_success() {
        BookingRequestDTO dto = buildRequest(
                LocalDate.now().plusDays(10),
                LocalTime.of(19, 0),
                LocalTime.of(23, 0)
        );

        Booking saved = new Booking();
        saved.setId(1L);
        saved.setRequesterName(dto.getRequesterName());
        saved.setRequesterEmail(dto.getRequesterEmail());
        saved.setRequesterPhone(dto.getRequesterPhone());
        saved.setEventName(dto.getEventName());
        saved.setEventDate(dto.getEventDate());
        saved.setStartTime(dto.getStartTime());
        saved.setEndTime(dto.getEndTime());
        saved.setLocation(dto.getLocation());
        saved.setStatus(BookingStatus.PENDING);

        when(bookingRepository.hasConflict(any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(saved);

        assertThatNoException().isThrownBy(() -> bookingService.create(dto));
        verify(bookingRepository).save(any());
    }

    @Test
    @DisplayName("Não deve permitir reativar agendamento cancelado")
    void updateStatus_cancelledIsTerminal() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L))
                .thenReturn(java.util.Optional.of(booking));

        var statusDTO = new com.artistshowcase.api.dto.BookingStatusUpdateDTO(
                BookingStatus.CONFIRMED
        );

        assertThatThrownBy(() -> bookingService.updateStatus(1L, statusDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cancelado");
    }
}