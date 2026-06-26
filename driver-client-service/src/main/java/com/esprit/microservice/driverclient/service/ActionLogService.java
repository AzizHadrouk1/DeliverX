package com.esprit.microservice.driverclient.service;

import com.esprit.microservice.driverclient.model.ActionLog;
import com.esprit.microservice.driverclient.repository.ActionLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActionLogService {

    private final ActionLogRepository actionLogRepository;

    public ActionLogService(ActionLogRepository actionLogRepository) {
        this.actionLogRepository = actionLogRepository;
    }

    public void log(String username, String action, String entityType, Long entityId, String details) {
        ActionLog log = new ActionLog();
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        actionLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<ActionLog> findAll(int page, int size, String entityType) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (entityType != null && !entityType.isBlank()) {
            return actionLogRepository.findByEntityTypeOrderByCreatedAtDesc(entityType, pageable);
        }
        return actionLogRepository.findAll(pageable);
    }
}
