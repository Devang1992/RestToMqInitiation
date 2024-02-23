package com.mqInitializer.MqMessageGeneration.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

@Slf4j
public abstract class Mq {
    protected ObjectMapper objectMapper;
    protected ConnectionFactory connectionFactory;
    protected JmsTemplate jmsTemplate;
    protected Connection connection;
    protected Session session;

    public void sendMessage(String destination, String payload, String correlationId) {
        checkInitJms();
        try {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setJMSCorrelationID(correlationId);
            textMessage.setText(payload);
            jmsTemplate.convertAndSend(destination, textMessage);
            log.info("MQ Message Sent. " + textMessage + payload);
        } catch (JMSException e) {
            log.error("JMSException. Unable to send message to " + destination + ".\n" + e.getMessage());
        }
    }

    protected void checkInitJms() {
        try {
            if (connection == null) {
                log.warn("connection == null");
                connection = this.connectionFactory.createConnection();
                session = connection.createSession();
            } else if (session == null) {
                session = connection.createSession();
            }
        } catch (JMSException e) {
            log.error("JMSException during JMS initialization. " + e.getMessage());
        }
    }
}
