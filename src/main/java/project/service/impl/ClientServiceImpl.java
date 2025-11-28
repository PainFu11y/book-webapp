package project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.mapper.ClientMapper;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + email));
        return clientMapper.toDTO(client);
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO clientDTO) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + email));

        if (!email.equals(clientDTO.getEmail())) {
            clientRepository.findByEmail(clientDTO.getEmail())
                    .ifPresent(c -> {
                        throw new AlreadyExistException("Email is already used: " + clientDTO.getEmail());
                    });
        }

        clientDTO.setPassword(passwordEncoder.encode(clientDTO.getPassword()));

        Client updatedClient = clientMapper.toEntity(clientDTO);
        updatedClient.setId(client.getId());
        updatedClient.setOrders(client.getOrders());

        clientRepository.save(updatedClient);
        return clientMapper.toDTO(updatedClient);
    }

    @Override
    public void deleteClientByEmail(String email) {
        clientRepository.deleteByEmail(email);
    }

    @Override
    public ClientDTO addClient(ClientDTO clientDTO) {
        clientRepository.findByEmail(clientDTO.getEmail())
                .ifPresent(c -> {
                    throw new AlreadyExistException("Email is already used: " + clientDTO.getEmail());
                });
        employeeRepository.findByEmail(clientDTO.getEmail())
                .ifPresent(c -> {
                    throw new AlreadyExistException("Email is already used: " + clientDTO.getEmail());
                });

        clientDTO.setPassword(passwordEncoder.encode(clientDTO.getPassword()));

        Client client = clientMapper.toEntity(clientDTO);
        client = clientRepository.save(client);
        return clientMapper.toDTO(client);
    }
}

