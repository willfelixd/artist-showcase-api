package com.artistshowcase.api.repository;

import com.artistshowcase.api.model.Booking;
import com.artistshowcase.api.model.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Busca agendamentos por status com paginação (para o painel admin)
    Page<Booking> findByStatusOrderByEventDateAscStartTimeAsc(
            BookingStatus status,
            Pageable pageable
    );

    // Busca todos ordenados por data (para o admin ver tudo)
    Page<Booking> findAllByOrderByEventDateAscStartTimeAsc(Pageable pageable);

    // Datas já ocupadas (para o frontend bloquear no calendário)
    @Query("""
            SELECT b.eventDate FROM Booking b
            WHERE b.status = 'CONFIRMED'
            AND b.eventDate >= :from
            """)
    List<LocalDate> findConfirmedDatesFrom(@Param("from") LocalDate from);

    // Query de conflito — o coração da regra de negócio
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.eventDate = :date
            AND b.status != 'CANCELLED'
            AND (
                (b.startTime < :endTime AND b.endTime > :startTime)
            )
            """)
    boolean hasConflict(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Mesma query de conflito, mas excluindo um ID (para updates)
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.eventDate = :date
            AND b.status != 'CANCELLED'
            AND b.id != :excludeId
            AND (
                (b.startTime < :endTime AND b.endTime > :startTime)
            )
            """)
    boolean hasConflictExcluding(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );
}