package com.epam.rd.autocode.spring.project.mapper;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.model.Employee;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {

        modelMapper.typeMap(EmployeeDTO.class, Employee.class)
                .addMappings(cfg -> {
                    cfg.skip(Employee::setOrders);// list of orders DTO not consist
                });


        modelMapper.typeMap(Employee.class, EmployeeDTO.class)
                .addMappings(cfg -> {
                    cfg.skip(EmployeeDTO::setPassword); //dont show password
                });
    }


    public Employee toEntity(EmployeeDTO dto) {
        return modelMapper.map(dto, Employee.class);
    }


    public EmployeeDTO toDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }
}
