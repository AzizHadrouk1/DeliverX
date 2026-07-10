package com.esprit.microservice.assignment.service;

import com.esprit.microservice.assignment.RabbitMQConfig;
import com.esprit.microservice.assignment.dto.AssignmentCreatedEvent;
import com.esprit.microservice.assignment.dto.AssignmentStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssignmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AssignmentEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public AssignmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAssignmentCreated(AssignmentCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.CREATED_ROUTING_KEY, event);
            log.info("Published assignment.created event for assignment {}", event.assignmentId());
        } catch (AmqpException e) {
            log.error("Failed to publish assignment.created event for assignment {}: {}",
                    event.assignmentId(), e.getMessage());
        }
    }

    public void publishStatusChanged(AssignmentStatusChangedEvent event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.STATUS_ROUTING_KEY, event);
            log.info("Published assignment.status.changed event for assignment {}: {} -> {}",
                    event.assignmentId(), event.previousStatus(), event.newStatus());
        } catch (AmqpException e) {
            log.error("Failed to publish assignment.status.changed event for assignment {}: {}",
                    event.assignmentId(), e.getMessage());
        }
    }
}
