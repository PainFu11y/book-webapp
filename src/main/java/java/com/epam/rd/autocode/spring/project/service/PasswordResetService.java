package java.com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.request.ForgotPasswordRequestDTO;
import com.epam.rd.autocode.spring.project.dto.request.ResetPasswordRequestDTO;
import jakarta.mail.MessagingException;

public interface PasswordResetService {
    void sendResetToken(ForgotPasswordRequestDTO dto) throws MessagingException;
    void resetPassword(ResetPasswordRequestDTO dto);
}
