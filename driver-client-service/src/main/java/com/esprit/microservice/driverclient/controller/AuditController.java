package com.esprit.microservice.driverclient.controller;

import com.esprit.microservice.driverclient.model.ActionLog;
import com.esprit.microservice.driverclient.service.ActionLogService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final ActionLogService actionLogService;

    public AuditController(ActionLogService actionLogService) {
        this.actionLogService = actionLogService;
    }

    @GetMapping
    public Page<ActionLog> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String entityType) {
        return actionLogService.findAll(page, size, entityType);
    }
}
