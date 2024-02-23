package com.mqInitializer.MqMessageGeneration.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqInitiator extends Mq{
    protected String requestQueue;
    private ApplicationContext applicationContext;

    public MqInitiator(ConnectionFactory connectionFactory,
                       JmsTemplate jmsTemplate,
                       ObjectMapper objectMapper,
                       ApplicationContext applicationContext) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplate = jmsTemplate;
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(String requestQueue, String payload, String correlationId, String env) {
        this.jmsTemplate = (JmsTemplate) applicationContext.getBean(env);
        this.connectionFactory = (ConnectionFactory) jmsTemplate.getConnectionFactory();
        this.requestQueue = requestQueue;
        sendMessage(requestQueue, payload,correlationId);
    }
}
