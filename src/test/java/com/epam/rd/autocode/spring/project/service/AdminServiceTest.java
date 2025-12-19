package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.AdminDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.model.Admin;
import com.epam.rd.autocode.spring.project.repo.AdminRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAdminsShouldReturnAdminDTOs() {
        Admin admin1 = Admin.builder().email("admin1@test.com").password("pass1").build();
        Admin admin2 = Admin.builder().email("admin2@test.com").password("pass2").build();

        when(adminRepository.findAll()).thenReturn(List.of(admin1, admin2));

        List<AdminDTO> result = adminService.getAllAdmins();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("admin1@test.com");
        assertThat(result.get(1).getEmail()).isEqualTo("admin2@test.com");
    }

    @Test
    void getAdminByEmailShouldReturnAdminDTO() {
        Admin admin = Admin.builder().email("admin@test.com").password("pass").build();
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        AdminDTO result = adminService.getAdminByEmail("admin@test.com");

        assertThat(result.getEmail()).isEqualTo("admin@test.com");
        assertThat(result.getPassword()).isNull();
    }

    @Test
    void getAdminByEmailShouldThrowExceptionIfNotFound() {
        when(adminRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.getAdminByEmail("notfound@test.com"));
    }

    @Test
    void addAdminShouldSaveAndReturnAdminDTO() {
        AdminDTO dto = new AdminDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("pass");

        when(adminRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        Admin savedAdmin = Admin.builder().email("new@test.com").password("encodedPass").build();
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        AdminDTO result = adminService.addAdmin(dto);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        assertThat(result.getPassword()).isNull();

        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void addAdminShouldThrowAlreadyExistExceptionIfEmailExists() {
        AdminDTO dto = new AdminDTO();
        dto.setEmail("existing@test.com");
        dto.setPassword("pass");

        when(adminRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(new Admin()));

        assertThrows(AlreadyExistException.class, () -> adminService.addAdmin(dto));
    }

    @Test
    void deleteAdminByEmailShouldDeleteAdmin() {
        Admin admin = Admin.builder().email("admin@test.com").build();
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        adminService.deleteAdminByEmail("admin@test.com");

        verify(adminRepository).delete(admin);
    }

    @Test
    void deleteAdminByEmailShouldThrowExceptionIfNotFound() {
        when(adminRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adminService.deleteAdminByEmail("notfound@test.com"));
    }
}
