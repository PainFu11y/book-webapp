package com.epam.rd.autocode.spring.project.service.impl.security;

import com.epam.rd.autocode.spring.project.model.Admin;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.AdminRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AdminRepository adminRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_ShouldReturnClient() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("pass");

        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        UserDetails userDetails = service.loadUserByUsername("client@example.com");

        assertThat(userDetails.getUsername()).isEqualTo(client.getEmail());
        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
    }

    @Test
    void loadUserByUsername_ShouldReturnEmployee() {
        Employee employee = new Employee();
        employee.setEmail("employee@example.com");
        employee.setPassword("pass");

        when(clientRepository.findByEmail("employee@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));

        UserDetails userDetails = service.loadUserByUsername("employee@example.com");

        assertThat(userDetails.getUsername()).isEqualTo(employee.getEmail());
        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));
    }

    @Test
    void loadUserByUsername_ShouldReturnAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("pass");

        when(clientRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        UserDetails userDetails = service.loadUserByUsername("admin@example.com");

        assertThat(userDetails.getUsername()).isEqualTo(admin.getEmail());
        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenNotFound() {
        when(clientRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsernameAndRole_ShouldReturnClient() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("pass");

        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        UserDetails userDetails = service.loadUserByUsernameAndRole("client@example.com", "CLIENT");

        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
    }

    @Test
    void loadUserByUsernameAndRole_ShouldThrow_WhenUnknownRole() {
        assertThatThrownBy(() -> service.loadUserByUsernameAndRole("email", "UNKNOWN"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Unknown role");
    }

    @Test
    void loadUserByUsernameAndRole_ShouldThrow_WhenUserNotFound() {
        when(clientRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsernameAndRole("notfound@example.com", "CLIENT"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Client not found");
    }
}
