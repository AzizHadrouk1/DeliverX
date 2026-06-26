package com.esprit.microservice.driverclient.controller;

import com.esprit.microservice.driverclient.dto.PageResponse;
import com.esprit.microservice.driverclient.model.Client;
import com.esprit.microservice.driverclient.model.ClientStatus;
import com.esprit.microservice.driverclient.model.ClientType;
import com.esprit.microservice.driverclient.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public Object getAllClients(
            @RequestParam(required = false) ClientStatus status,
            @RequestParam(required = false) ClientType type,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        if (page != null || (q != null && !q.isBlank())) {
            return clientService.search(q, status, type, page != null ? page : 0, size, sortBy, direction);
        }
        if (status != null) {
            return clientService.findByStatus(status);
        }
        if (type != null) {
            return clientService.findByType(type);
        }
        return clientService.findAll();
    }

    @GetMapping("/search")
    public PageResponse<Client> searchClients(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ClientStatus status,
            @RequestParam(required = false) ClientType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        return clientService.search(q, status, type, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping({"/create", ""})
    @ResponseStatus(HttpStatus.CREATED)
    public Client createClient(@Valid @RequestBody Client client) {
        return clientService.create(client);
    }

    @PutMapping("/{id}")
    public Client updateClient(@PathVariable Long id, @Valid @RequestBody Client client) {
        return clientService.update(id, client);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clientService.delete(id);
    }
}
