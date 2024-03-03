package com.vitor.roomservice.roomservice;

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

    public static final String CHECK_AVAILABILITY_QUEUE = "check-availability-queue";
    public static final String RESERVATIONS_EXCHANGE = "reservations-exchange";
    public static final String CHECK_AVAILABILITY_ROUTING_KEY = "check.availability";

    @Bean
    Queue checkAvailabilityQueue() {
        return new Queue(CHECK_AVAILABILITY_QUEUE, true);
    }

    @Bean
    TopicExchange reservationsExchange() {
        return new TopicExchange(RESERVATIONS_EXCHANGE);
    }

    @Bean
    Binding checkAvailabilityBinding(Queue checkAvailabilityQueue, TopicExchange reservationsExchange) {
        return BindingBuilder.bind(checkAvailabilityQueue).to(reservationsExchange).with(CHECK_AVAILABILITY_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
