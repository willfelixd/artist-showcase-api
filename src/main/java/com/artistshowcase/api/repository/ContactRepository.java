package com.artistshowcase.api.repository;

import com.artistshowcase.api.model.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactMessage, Long> {

    Page<ContactMessage> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Para o admin ver mensagens que falharam no envio de e-mail
    Page<ContactMessage> findByEmailSentFalseOrderByCreatedAtDesc(Pageable pageable);
}