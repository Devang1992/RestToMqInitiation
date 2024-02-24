package com.mqInitializer.MqMessageGeneration.service;

import com.mqInitializer.MqMessageGeneration.broker.MqInitiator;
import com.mqInitializer.MqMessageGeneration.broker.QueueProperties;
import com.mqInitializer.MqMessageGeneration.domain.ResponseEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MqServiceTest {
    private MqService mqService;
    private MqInitiator mqinitiator;
    @Autowired
    private QueueProperties queueProperties;
    public static final String CORRELATION_ID_HEADER_NAME = "jms-correlation-id";
    public static final String ENVIRONMENT_HEADER_NAME = "env-id";
    private HttpHeaders httpHeaders;
    private ResponseEntities responseEntities;
    private String requestBody;
    private String jmsCorrelationId;
    private String envId;

    @BeforeEach
    void init() {
        requestBody = "{TestBody}";
        jmsCorrelationId = "Test1";
        envId = "local";
        responseEntities = new ResponseEntities("JSON", jmsCorrelationId, "DEV.QUEUE.1");
        httpHeaders = new HttpHeaders();
        httpHeaders.set(CORRELATION_ID_HEADER_NAME, jmsCorrelationId);
        httpHeaders.set(ENVIRONMENT_HEADER_NAME, envId);
        mqinitiator = mock(MqInitiator.class);
        mqService = new MqService(mqinitiator, queueProperties);
        doNothing().when(mqinitiator).sendMessage(any(), any(), any(), any());
    }

    @Test
    void mqServiceInitiator() {
        ResponseEntities testEntities = mqService.mqServiceInitiator(httpHeaders, requestBody);
        assertEquals(responseEntities, testEntities);
        verify(mqinitiator, times(1)).sendMessage("DEV.QUEUE.1",
                requestBody, jmsCorrelationId, envId);
    }
}