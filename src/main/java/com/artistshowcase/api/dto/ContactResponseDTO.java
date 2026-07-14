package com.artistshowcase.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponseDTO implements Serializable {
    private Long id;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String subject;
    private String message;
    private boolean emailSent;
    private LocalDateTime createdAt;
}