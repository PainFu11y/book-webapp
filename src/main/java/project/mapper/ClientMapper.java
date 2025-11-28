package project.mapper;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.model.Client;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {

        // ClientDTO to Client
        modelMapper.typeMap(ClientDTO.class, Client.class)
                .addMappings(cfg -> {
                    cfg.skip(Client::setOrders); // list of orders DTO not consist
                    cfg.skip(Client::setId);     // ID inserts JPA
                });

        // Client to ClientDTO
        modelMapper.typeMap(Client.class, ClientDTO.class)
                .addMappings(cfg -> {
                    cfg.skip(ClientDTO::setPassword); // dont show password
                });
    }


    public Client toEntity(ClientDTO dto) {
        return modelMapper.map(dto, Client.class);
    }


    public ClientDTO toDTO(Client client) {
        return modelMapper.map(client, ClientDTO.class);
    }
}
