package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.mapper.EmployeeMapper;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found with email: " + email));
        return employeeMapper.toDTO(employee);
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + email));

        if (!email.equals(employeeDTO.getEmail())) {
            employeeRepository.findByEmail(employeeDTO.getEmail())
                    .ifPresent(e -> {
                        throw new AlreadyExistException("Email is already used by another employee: " + employeeDTO.getEmail());
                    });
        }

        if (employeeDTO.getPhone() != null && !employeeDTO.getPhone().equals(employee.getPhone())) {
            employeeRepository.findByPhone(employeeDTO.getPhone())
                    .ifPresent(e -> {
                        throw new AlreadyExistException("Phone number is already used by another employee: " + employeeDTO.getPhone());
                    });
        }

        employeeDTO.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));

        Employee updatedEmployee = employeeMapper.toEntity(employeeDTO);
        updatedEmployee.setId(employee.getId());
        updatedEmployee.setOrders(employee.getOrders());

        employeeRepository.save(updatedEmployee);
        return employeeMapper.toDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployeeByEmail(String email) {
        employeeRepository.deleteByEmail(email);
    }

    @Override
    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        employeeRepository.findByEmail(employeeDTO.getEmail())
                .ifPresent(e -> {
                    throw new AlreadyExistException("Email is already used: " + employeeDTO.getEmail());
                });
        clientRepository.findByEmail(employeeDTO.getEmail())
                .ifPresent(e -> {
                    throw new AlreadyExistException("Email is already used: " + employeeDTO.getEmail());
                });


        if (employeeDTO.getPhone() != null) {
            employeeRepository.findByPhone(employeeDTO.getPhone())
                    .ifPresent(e -> {
                        throw new AlreadyExistException("Phone number is already used: " + employeeDTO.getPhone());
                    });
        }

        employeeDTO.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));

        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee = employeeRepository.save(employee);
        return employeeMapper.toDTO(employee);
    }
}
