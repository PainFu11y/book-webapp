package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.mapper.ClientMapper;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.impl.ClientServiceImpl;
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

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllClientsShouldReturnClientDTOs() {
        Client client1 = new Client();
        client1.setEmail("client1@test.com");
        Client client2 = new Client();
        client2.setEmail("client2@test.com");

        ClientDTO dto1 = new ClientDTO();
        dto1.setEmail("client1@test.com");
        ClientDTO dto2 = new ClientDTO();
        dto2.setEmail("client2@test.com");

        when(clientRepository.findAll()).thenReturn(List.of(client1, client2));
        when(clientMapper.toDTO(client1)).thenReturn(dto1);
        when(clientMapper.toDTO(client2)).thenReturn(dto2);

        List<ClientDTO> result = clientService.getAllClients();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("client1@test.com");
        assertThat(result.get(1).getEmail()).isEqualTo("client2@test.com");
    }

    @Test
    void getClientByEmailShouldReturnClientDTO() {
        Client client = new Client();
        client.setEmail("client@test.com");
        ClientDTO dto = new ClientDTO();
        dto.setEmail("client@test.com");

        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));
        when(clientMapper.toDTO(client)).thenReturn(dto);

        ClientDTO result = clientService.getClientByEmail("client@test.com");

        assertThat(result.getEmail()).isEqualTo("client@test.com");
    }

    @Test
    void getClientByEmailShouldThrowNotFoundException() {
        when(clientRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.getClientByEmail("notfound@test.com"));
    }

    @Test
    void addClientShouldSaveAndReturnClientDTO() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("pass");

        Client entity = new Client();
        entity.setEmail("new@test.com");

        ClientDTO savedDto = new ClientDTO();
        savedDto.setEmail("new@test.com");

        when(clientRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(clientMapper.toEntity(dto)).thenReturn(entity);
        when(clientRepository.save(entity)).thenReturn(entity);
        when(clientMapper.toDTO(entity)).thenReturn(savedDto);

        ClientDTO result = clientService.addClient(dto);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        verify(clientRepository).save(entity);
    }

    @Test
    void addClientShouldThrowAlreadyExistExceptionIfEmailExists() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("existing@test.com");

        when(clientRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(new Client()));

        assertThrows(AlreadyExistException.class, () -> clientService.addClient(dto));
    }

    @Test
    void updateClientByEmailShouldUpdateClient() {
        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setEmail("old@test.com");

        ClientDTO dto = new ClientDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("newpass");

        Client updatedEntity = new Client();
        updatedEntity.setId(1L);
        updatedEntity.setEmail("new@test.com");

        ClientDTO updatedDto = new ClientDTO();
        updatedDto.setEmail("new@test.com");

        when(clientRepository.findByEmail("old@test.com")).thenReturn(Optional.of(existingClient));
        when(clientRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");
        when(clientMapper.toEntity(dto)).thenReturn(updatedEntity);
        when(clientMapper.toDTO(updatedEntity)).thenReturn(updatedDto);

        ClientDTO result = clientService.updateClientByEmail("old@test.com", dto);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        verify(clientRepository).save(updatedEntity);
    }

    @Test
    void updateClientByEmailShouldThrowNotFoundExceptionIfClientNotFound() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("any@test.com");

        when(clientRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.updateClientByEmail("notfound@test.com", dto));
    }

    @Test
    void deleteClientByEmailShouldCallRepository() {
        clientService.deleteClientByEmail("client@test.com");

        verify(clientRepository).deleteByEmail("client@test.com");
    }
}
