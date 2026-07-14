package com.artistshowcase.api.model.enums;

public enum BookingStatus {
    PENDING,    // Solicitação recebida, aguardando confirmação do admin
    CONFIRMED,  // Admin confirmou o show
    CANCELLED   // Admin ou sistema cancelou
}