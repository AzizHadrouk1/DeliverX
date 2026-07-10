package com.esprit.microservice.assignment;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "assignment.exchange";
    public static final String CREATED_QUEUE = "assignment.created.queue";
    public static final String STATUS_QUEUE = "assignment.status.queue";
    public static final String CREATED_ROUTING_KEY = "assignment.created";
    public static final String STATUS_ROUTING_KEY = "assignment.status.changed";

    @Bean
    public TopicExchange assignmentExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue assignmentCreatedQueue() {
        return new Queue(CREATED_QUEUE, true);
    }

    @Bean
    public Queue assignmentStatusQueue() {
        return new Queue(STATUS_QUEUE, true);
    }

    @Bean
    public Binding createdBinding() {
        return BindingBuilder.bind(assignmentCreatedQueue()).to(assignmentExchange()).with(CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding statusBinding() {
        return BindingBuilder.bind(assignmentStatusQueue()).to(assignmentExchange()).with(STATUS_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
