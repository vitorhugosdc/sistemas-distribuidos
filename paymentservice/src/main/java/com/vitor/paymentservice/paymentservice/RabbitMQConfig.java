package com.vitor.paymentservice.paymentservice;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PROCESS_PAYMENT_QUEUE = "process-payment-queue";
    public static final String PAYMENT_CONFIRMATION_QUEUE = "payment-confirmation-queue";
    public static final String PAYMENTS_EXCHANGE = "payments-exchange";
    public static final String PROCESS_PAYMENT_ROUTING_KEY = "process.payment";
    public static final String PAYMENT_CONFIRMATION_ROUTING_KEY = "payment.confirmation";

    @Bean
    Queue processPaymentQueue() {
        return new Queue(PROCESS_PAYMENT_QUEUE, true);
    }

    @Bean
    Queue paymentConfirmationQueue() {
        return new Queue(PAYMENT_CONFIRMATION_QUEUE, true);
    }

    @Bean
    TopicExchange paymentsExchange() {
        return new TopicExchange(PAYMENTS_EXCHANGE);
    }

    @Bean
    Binding processPaymentBinding(Queue processPaymentQueue, TopicExchange paymentsExchange) {
        return BindingBuilder.bind(processPaymentQueue).to(paymentsExchange).with(PROCESS_PAYMENT_ROUTING_KEY);
    }

    @Bean
    Binding paymentConfirmationBinding(Queue paymentConfirmationQueue, TopicExchange paymentsExchange) {
        return BindingBuilder.bind(paymentConfirmationQueue).to(paymentsExchange).with(PAYMENT_CONFIRMATION_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
