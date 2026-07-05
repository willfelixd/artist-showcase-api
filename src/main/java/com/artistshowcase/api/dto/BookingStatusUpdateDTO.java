package com.artistshowcase.api.dto;

import com.artistshowcase.api.model.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdateDTO {

    @NotNull(message = "Status é obrigatório")
    private BookingStatus status;
}