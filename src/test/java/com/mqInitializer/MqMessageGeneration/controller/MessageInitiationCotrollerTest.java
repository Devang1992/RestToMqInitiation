package com.mqInitializer.MqMessageGeneration.controller;

import com.mqInitializer.MqMessageGeneration.domain.ResponseEntities;
import com.mqInitializer.MqMessageGeneration.service.MqService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageInitiationCotrollerTest {

    private MessageInitiationCotroller messageInitiationCotroller;
    private MqService mqService;
    private HttpHeaders httpHeaders;
    private ResponseEntities responseEntities;
    private String requestBody;

    @BeforeEach
    void init() {
        requestBody = "TestBody";
        responseEntities = new ResponseEntities("JSON", "Test1", "Destination");
        httpHeaders = new HttpHeaders();
        httpHeaders.set("jms-correlation-id", "Test1");
        mqService = mock(MqService.class);
        messageInitiationCotroller = new MessageInitiationCotroller(mqService);
        when(mqService.mqServiceInitiator(any(), any())).
                thenReturn(responseEntities);
    }

    @Test
    void mqMessageInitiator() {
        ResponseEntities testEntities = messageInitiationCotroller.mqMessageInitiator(httpHeaders, requestBody);
        assertEquals(responseEntities, testEntities);
        verify(mqService, times(1)).mqServiceInitiator(httpHeaders, requestBody);
    }
}