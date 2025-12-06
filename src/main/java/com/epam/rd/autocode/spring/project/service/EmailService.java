package com.epam.rd.autocode.spring.project.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendResetPasswordEmail(String to, String token) throws MessagingException;
}
