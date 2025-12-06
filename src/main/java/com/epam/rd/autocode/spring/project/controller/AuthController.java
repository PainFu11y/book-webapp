package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.request.ForgotPasswordRequestDTO;
import com.epam.rd.autocode.spring.project.dto.request.ResetPasswordRequestDTO;
import com.epam.rd.autocode.spring.project.enums.UserRole;
import com.epam.rd.autocode.spring.project.model.request.AuthRequest;
import com.epam.rd.autocode.spring.project.service.impl.auth.AuthService;
import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final PasswordResetService passwordResetService;
    private final AuthService authService;

    @PostMapping("/login/client")
    @Operation(summary = "Login as client")
    public ResponseEntity<?> loginClient(@RequestBody AuthRequest dto) {
        return authService.authenticate(dto.getEmail(), dto.getPassword(), UserRole.CLIENT.toString());
    }

    @PostMapping("/login/employee")
    @Operation(summary = "Login as employee")
    public ResponseEntity<?> loginEmployee(@RequestBody AuthRequest dto) {
        return authService.authenticate(dto.getEmail(), dto.getPassword(), UserRole.EMPLOYEE.toString());
    }

    @PostMapping("/login/admin")
    @Operation(summary = "Login as admin")
    public ResponseEntity<?> loginAdmin(@RequestBody AuthRequest dto) {
        return authService.authenticate(dto.getEmail(), dto.getPassword(), UserRole.ADMIN.toString());
    }


    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset token")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO dto) throws MessagingException {
        passwordResetService.sendResetToken(dto);
        return ResponseEntity.ok("Reset token sent to email");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO dto) {
        passwordResetService.resetPassword(dto);
        return ResponseEntity.ok("Password has been reset successfully");
    }
}
