package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.mapper.EmployeeMapper;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.impl.EmployeeServiceImpl;
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

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEmployeesShouldReturnEmployeeDTOs() {
        Employee emp1 = new Employee();
        emp1.setEmail("emp1@test.com");
        Employee emp2 = new Employee();
        emp2.setEmail("emp2@test.com");

        EmployeeDTO dto1 = new EmployeeDTO();
        dto1.setEmail("emp1@test.com");
        EmployeeDTO dto2 = new EmployeeDTO();
        dto2.setEmail("emp2@test.com");

        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));
        when(employeeMapper.toDTO(emp1)).thenReturn(dto1);
        when(employeeMapper.toDTO(emp2)).thenReturn(dto2);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("emp1@test.com");
        assertThat(result.get(1).getEmail()).isEqualTo("emp2@test.com");
    }

    @Test
    void getEmployeeByEmailShouldReturnEmployeeDTO() {
        Employee employee = new Employee();
        employee.setEmail("emp@test.com");
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("emp@test.com");

        when(employeeRepository.findByEmail("emp@test.com")).thenReturn(Optional.of(employee));
        when(employeeMapper.toDTO(employee)).thenReturn(dto);

        EmployeeDTO result = employeeService.getEmployeeByEmail("emp@test.com");

        assertThat(result.getEmail()).isEqualTo("emp@test.com");
    }

    @Test
    void getEmployeeByEmailShouldThrowRuntimeException() {
        when(employeeRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.getEmployeeByEmail("notfound@test.com"));
    }

    @Test
    void addEmployeeShouldSaveAndReturnDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("pass");
        dto.setPhone("12345");

        Employee entity = new Employee();
        entity.setEmail("new@test.com");
        entity.setPhone("12345");

        EmployeeDTO savedDto = new EmployeeDTO();
        savedDto.setEmail("new@test.com");

        when(employeeRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(clientRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByPhone("12345")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(employeeMapper.toEntity(dto)).thenReturn(entity);
        when(employeeRepository.save(entity)).thenReturn(entity);
        when(employeeMapper.toDTO(entity)).thenReturn(savedDto);

        EmployeeDTO result = employeeService.addEmployee(dto);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        verify(employeeRepository).save(entity);
    }

    @Test
    void addEmployeeShouldThrowAlreadyExistExceptionIfEmailExists() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("existing@test.com");

        when(employeeRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(new Employee()));

        assertThrows(AlreadyExistException.class, () -> employeeService.addEmployee(dto));
    }

    @Test
    void updateEmployeeByEmailShouldUpdateEmployee() {
        Employee existing = new Employee();
        existing.setId(1L);
        existing.setEmail("old@test.com");
        existing.setPhone("11111");

        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("newpass");
        dto.setPhone("22222");

        Employee updatedEntity = new Employee();
        updatedEntity.setId(1L);
        updatedEntity.setEmail("new@test.com");
        updatedEntity.setPhone("22222");

        EmployeeDTO updatedDto = new EmployeeDTO();
        updatedDto.setEmail("new@test.com");

        when(employeeRepository.findByEmail("old@test.com")).thenReturn(Optional.of(existing));
        when(employeeRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByPhone("22222")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");
        when(employeeMapper.toEntity(dto)).thenReturn(updatedEntity);
        when(employeeMapper.toDTO(updatedEntity)).thenReturn(updatedDto);

        EmployeeDTO result = employeeService.updateEmployeeByEmail("old@test.com", dto);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        verify(employeeRepository).save(updatedEntity);
    }

    @Test
    void updateEmployeeByEmailShouldThrowNotFoundException() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("any@test.com");

        when(employeeRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> employeeService.updateEmployeeByEmail("notfound@test.com", dto));
    }

    @Test
    void deleteEmployeeByEmailShouldCallRepository() {
        employeeService.deleteEmployeeByEmail("emp@test.com");

        verify(employeeRepository).deleteByEmail("emp@test.com");
    }
}

