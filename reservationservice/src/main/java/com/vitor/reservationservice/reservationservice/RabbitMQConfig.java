package com.vitor.reservationservice.reservationservice;

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

    public static final String MAKE_RESERVATION_QUEUE = "make-reservation-queue";
    public static final String RESERVATIONS_EXCHANGE = "reservations-exchange";
    public static final String MAKE_RESERVATION_ROUTING_KEY = "make.reservation";

    @Bean
    Queue makeReservationQueue() {
        return new Queue(MAKE_RESERVATION_QUEUE, true);
    }

    @Bean
    TopicExchange reservationsExchange() {
        return new TopicExchange(RESERVATIONS_EXCHANGE);
    }

    @Bean
    Binding makeReservationBinding(Queue makeReservationQueue, TopicExchange reservationsExchange) {
        return BindingBuilder.bind(makeReservationQueue).to(reservationsExchange).with(MAKE_RESERVATION_ROUTING_KEY);
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
