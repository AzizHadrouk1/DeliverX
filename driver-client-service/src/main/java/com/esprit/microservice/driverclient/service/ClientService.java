package com.esprit.microservice.driverclient.service;

import com.esprit.microservice.driverclient.dto.PageResponse;
import com.esprit.microservice.driverclient.model.Client;
import com.esprit.microservice.driverclient.model.ClientStatus;
import com.esprit.microservice.driverclient.model.ClientType;
import com.esprit.microservice.driverclient.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final ActionLogService actionLogService;

    public ClientService(ClientRepository clientRepository, ActionLogService actionLogService) {
        this.clientRepository = clientRepository;
        this.actionLogService = actionLogService;
    }

    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PageResponse<Client> search(String q, ClientStatus status, ClientType type,
                                       int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Client> result = clientRepository.search(q, status, type, pageable);
        return toPageResponse(result);
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Client> findByStatus(ClientStatus status) {
        return clientRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Client> findByType(ClientType type) {
        return clientRepository.findByType(type);
    }

    public Client create(Client client) {
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Client with email already exists: " + client.getEmail());
        }
        Client saved = clientRepository.save(client);
        actionLogService.log("system", "CREATE", "Client", saved.getId(), saved.getEmail());
        return saved;
    }

    public Client update(Long id, Client updatedClient) {
        Client existing = findById(id);

        if (!existing.getEmail().equals(updatedClient.getEmail())
                && clientRepository.existsByEmail(updatedClient.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Client with email already exists: " + updatedClient.getEmail());
        }

        existing.setFirstName(updatedClient.getFirstName());
        existing.setLastName(updatedClient.getLastName());
        existing.setEmail(updatedClient.getEmail());
        existing.setPhone(updatedClient.getPhone());
        existing.setCompanyName(updatedClient.getCompanyName());
        existing.setAddress(updatedClient.getAddress());
        existing.setCity(updatedClient.getCity());
        existing.setType(updatedClient.getType());
        existing.setStatus(updatedClient.getStatus());

        Client saved = clientRepository.save(existing);
        actionLogService.log("system", "UPDATE", "Client", saved.getId(), saved.getEmail());
        return saved;
    }

    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + id);
        }
        clientRepository.deleteById(id);
        actionLogService.log("system", "DELETE", "Client", id, null);
    }

    private PageResponse<Client> toPageResponse(Page<Client> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
