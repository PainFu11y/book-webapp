package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.AdminDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.model.Admin;
import com.epam.rd.autocode.spring.project.repo.AdminRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminDTO> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public AdminDTO getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return mapToDto(admin);
    }

    public AdminDTO addAdmin(AdminDTO dto) {
        adminRepository.findByEmail(dto.getEmail()).ifPresent(e -> {
                    throw new AlreadyExistException("Email is already used: " + dto.getEmail());
                });
        employeeRepository.findByEmail(dto.getEmail()).ifPresent(e -> {
            throw new AlreadyExistException("Email is already used: " + dto.getEmail());
        });
        clientRepository.findByEmail(dto.getEmail()).ifPresent(e -> {
            throw new AlreadyExistException("Email is already used: " + dto.getEmail());
        });

        Admin admin = Admin.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return mapToDto(adminRepository.save(admin));
    }

    public void deleteAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        adminRepository.delete(admin);
    }

    private AdminDTO mapToDto(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setEmail(admin.getEmail());
        dto.setPassword(null);
        return dto;
    }
}
