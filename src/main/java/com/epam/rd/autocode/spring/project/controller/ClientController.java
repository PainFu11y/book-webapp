package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "Get all clients")
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get client by email")
    public ClientDTO getClientByEmail(@PathVariable String email) {
        return clientService.getClientByEmail(email);
    }

    @PostMapping
    @Operation(summary = "Add a new client")
    public ResponseEntity<ClientDTO> addClient(@Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO savedClient = clientService.addClient(clientDTO);
        return ResponseEntity.ok(savedClient);
    }

    @PutMapping("/{email}")
    @Operation(summary = "Update client by email")
    public ClientDTO updateClient(@PathVariable String email, @Valid @RequestBody ClientDTO clientDTO) {
        return clientService.updateClientByEmail(email, clientDTO);
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete client by email")
    public ResponseEntity<Void> deleteClient(@PathVariable String email) {
        clientService.deleteClientByEmail(email);
        return ResponseEntity.noContent().build();
    }
}

