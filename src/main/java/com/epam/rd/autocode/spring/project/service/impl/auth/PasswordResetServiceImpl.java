package com.epam.rd.autocode.spring.project.service.impl.auth;


import com.epam.rd.autocode.spring.project.dto.request.ForgotPasswordRequest;
import com.epam.rd.autocode.spring.project.dto.request.ResetPasswordRequest;
import com.epam.rd.autocode.spring.project.enums.UserRole;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.AdminRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.PasswordResetTokenRepository;
import com.epam.rd.autocode.spring.project.service.EmailService;
import com.epam.rd.autocode.spring.project.service.PasswordResetService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final AdminRepository adminRepo;
    private final EmployeeRepository employeeRepo;
    private final ClientRepository clientRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public void sendResetToken(ForgotPasswordRequest dto) throws MessagingException {
        Long userId;
        UserRole role;

        Optional<Admin> adminOpt = adminRepo.findByEmail(dto.getEmail());
        Optional<Employee> employeeOpt = employeeRepo.findByEmail(dto.getEmail());
        Optional<Client> clientOpt = clientRepo.findByEmail(dto.getEmail());

        if (adminOpt.isPresent()) {
            userId = adminOpt.get().getId();
            role = UserRole.ADMIN;
        } else if (employeeOpt.isPresent()) {
            userId = employeeOpt.get().getId();
            role = UserRole.EMPLOYEE;
        } else if (clientOpt.isPresent()) {
            userId = clientOpt.get().getId();
            role = UserRole.CLIENT;
        } else {
            throw new NotFoundException("Email not found");
        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(userId)
                .role(role)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        tokenRepo.save(resetToken);

        emailService.sendResetPasswordEmail(dto.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest dto) {
        PasswordResetToken token = tokenRepo.findByToken(dto.getToken())
                .orElseThrow(() -> new NotFoundException("Invalid token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        switch (token.getRole()) {
            case ADMIN -> {
                Admin admin = adminRepo.findById(token.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
                admin.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                adminRepo.save(admin);
            }
            case EMPLOYEE -> {
                Employee emp = employeeRepo.findById(token.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
                emp.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                employeeRepo.save(emp);
            }
            case CLIENT -> {
                Client client = clientRepo.findById(token.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
                client.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                clientRepo.save(client);
            }
        }

        tokenRepo.delete(token);
    }
}