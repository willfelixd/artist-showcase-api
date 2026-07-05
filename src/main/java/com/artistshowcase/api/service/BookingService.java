package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.BookingRequestDTO;
import com.artistshowcase.api.dto.BookingResponseDTO;
import com.artistshowcase.api.dto.BookingStatusUpdateDTO;
import com.artistshowcase.api.exception.BookingConflictException;
import com.artistshowcase.api.model.Booking;
import com.artistshowcase.api.model.enums.BookingStatus;
import com.artistshowcase.api.repository.BookingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Público: solicitar agendamento
    public BookingResponseDTO create(BookingRequestDTO dto) {
        validateTimes(dto.getStartTime(), dto.getEndTime());
        checkConflict(dto.getEventDate(), dto.getStartTime(), dto.getEndTime(), null);

        Booking booking = new Booking();
        booking.setRequesterName(dto.getRequesterName());
        booking.setRequesterEmail(dto.getRequesterEmail());
        booking.setRequesterPhone(dto.getRequesterPhone());
        booking.setEventName(dto.getEventName());
        booking.setEventDate(dto.getEventDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setLocation(dto.getLocation());
        booking.setNotes(dto.getNotes());

        return toResponseDTO(bookingRepository.save(booking));
    }

    // Admin: listar todos
    public Page<BookingResponseDTO> findAll(int page, int size) {
        return bookingRepository
                .findAllByOrderByEventDateAscStartTimeAsc(PageRequest.of(page, size))
                .map(this::toResponseDTO);
    }

    // Admin: filtrar por status
    public Page<BookingResponseDTO> findByStatus(BookingStatus status, int page, int size) {
        return bookingRepository
                .findByStatusOrderByEventDateAscStartTimeAsc(status, PageRequest.of(page, size))
                .map(this::toResponseDTO);
    }

    // Público: buscar um agendamento por ID
    public BookingResponseDTO findById(Long id) {
        return bookingRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado: " + id));
    }

    // Público: datas bloqueadas para o calendário
    public List<LocalDate> getUnavailableDates() {
        return bookingRepository.findConfirmedDatesFrom(LocalDate.now());
    }

    // Admin: atualizar status
    public BookingResponseDTO updateStatus(Long id, BookingStatusUpdateDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado: " + id));

        validateStatusTransition(booking.getStatus(), dto.getStatus());
        booking.setStatus(dto.getStatus());

        return toResponseDTO(bookingRepository.save(booking));
    }

    // Admin: deletar
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new NoSuchElementException("Agendamento não encontrado: " + id);
        }
        bookingRepository.deleteById(id);
    }

    // --- Regras de negócio privadas ---

    private void validateTimes(
            java.time.LocalTime startTime,
            java.time.LocalTime endTime
    ) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "Horário de fim deve ser após o horário de início"
            );
        }
    }

    private void checkConflict(
            LocalDate date,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime,
            Long excludeId
    ) {
        boolean conflict = excludeId == null
                ? bookingRepository.hasConflict(date, startTime, endTime)
                : bookingRepository.hasConflictExcluding(date, startTime, endTime, excludeId);

        if (conflict) {
            throw new BookingConflictException(
                    "Já existe um show agendado nesse horário em " + date
            );
        }
    }

    private void validateStatusTransition(BookingStatus current, BookingStatus next) {
        // Não pode reativar um cancelado
        if (current == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Não é possível alterar um agendamento cancelado"
            );
        }
        // Não pode "desconfirmar" — só cancelar
        if (current == BookingStatus.CONFIRMED && next == BookingStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Não é possível voltar um agendamento confirmado para pendente"
            );
        }
    }

    private BookingResponseDTO toResponseDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getRequesterName(),
                booking.getRequesterEmail(),
                booking.getRequesterPhone(),
                booking.getEventName(),
                booking.getEventDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getLocation(),
                booking.getNotes(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}