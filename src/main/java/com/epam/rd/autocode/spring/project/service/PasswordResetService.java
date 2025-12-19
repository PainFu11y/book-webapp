package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.request.ForgotPasswordRequest;
import com.epam.rd.autocode.spring.project.dto.request.ResetPasswordRequest;
import jakarta.mail.MessagingException;

public interface PasswordResetService {
    void sendResetToken(ForgotPasswordRequest dto) throws MessagingException;
    void resetPassword(ResetPasswordRequest dto);
}
