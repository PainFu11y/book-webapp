package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import com.epam.rd.autocode.spring.project.service.impl.ClientBlockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ClientService clientService;
    private final ClientBlockService clientBlockService;

    @GetMapping
    @Operation(summary = "Get all employees")
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/customers")
    @Operation(summary = "Get all customers")
    public List<ClientDTO> getCustomers() {
        return clientService.getAllClients();
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get employee by email")
    public EmployeeDTO getEmployeeByEmail(@PathVariable String email) {
        return employeeService.getEmployeeByEmail(email);
    }


    @PostMapping
    @Operation(summary = "Add a new employee")
    public ResponseEntity<EmployeeDTO> addEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO savedEmployee = employeeService.addEmployee(employeeDTO);
        return ResponseEntity.ok(savedEmployee);
    }

    @PutMapping("/{email}")
    @Operation(summary = "Update employee by email")
    public EmployeeDTO updateEmployee(@PathVariable String email, @Valid @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.updateEmployeeByEmail(email, employeeDTO);
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete employee by email")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        employeeService.deleteEmployeeByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/block-client")
    @Operation(summary = "Block client by email")
    public ResponseEntity<?> blockClient(@RequestParam String email) {
        clientBlockService.blockClient(email);
        return ResponseEntity.ok(Map.of("status", "Client blocked"));
    }

    @PostMapping("/unblock-client")
    @Operation(summary = "Unblock client by email")
    public ResponseEntity<?> unblockClient(@RequestParam String email) {
        clientBlockService.unblockClient(email);
        return ResponseEntity.ok(Map.of("status", "Client unblocked"));
    }
}
