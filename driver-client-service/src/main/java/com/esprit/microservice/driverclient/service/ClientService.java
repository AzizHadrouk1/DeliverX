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
    private final UniquenessGuard uniquenessGuard;

    public ClientService(ClientRepository clientRepository, UniquenessGuard uniquenessGuard) {
        this.clientRepository = clientRepository;
        this.uniquenessGuard = uniquenessGuard;
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
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No client record linked to this account: " + email));
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
        uniquenessGuard.check(clientRepository.existsByEmail(client.getEmail()),
                "Client with email already exists: " + client.getEmail());

        return clientRepository.save(client);
    }

    public Client update(Long id, Client updatedClient) {
        Client existing = findById(id);

        if (!existing.getEmail().equals(updatedClient.getEmail())) {
            uniquenessGuard.check(clientRepository.existsByEmail(updatedClient.getEmail()),
                    "Client with email already exists: " + updatedClient.getEmail());
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

        return clientRepository.save(existing);
    }

    public Client updateOwnProfile(String email, Client updatedClient) {
        Client existing = findByEmail(email);

        existing.setFirstName(updatedClient.getFirstName());
        existing.setLastName(updatedClient.getLastName());
        existing.setPhone(updatedClient.getPhone());
        existing.setCompanyName(updatedClient.getCompanyName());
        existing.setAddress(updatedClient.getAddress());
        existing.setCity(updatedClient.getCity());

        return clientRepository.save(existing);
    }

    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found: " + id);
        }
        clientRepository.deleteById(id);
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
