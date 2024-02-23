package com.mqInitializer.MqMessageGeneration.controller;

import com.mqInitializer.MqMessageGeneration.domain.ResponseEntities;
import com.mqInitializer.MqMessageGeneration.service.MqService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageInitiationCotrollerTest {

    private MessageInitiationCotroller messageInitiationCotroller;
    private MqService mqService;
    private HttpHeaders httpHeaders;
    private ResponseEntities responseEntities;

    @BeforeEach
    void init() {
        responseEntities = new ResponseEntities("Test", "Test1", "Destination");
        httpHeaders = new HttpHeaders();
        httpHeaders.set("jms-correlation-id", "Test1");
        mqService = mock(MqService.class);
        messageInitiationCotroller = new MessageInitiationCotroller(mqService);
        when(mqService.mqServiceInitiator(any(), any())).
                thenReturn(responseEntities);
    }

    @Test
    void mqMessageInitiator() {
        assertEquals(responseEntities,
                messageInitiationCotroller.mqMessageInitiator(httpHeaders, "Test"));
    }
}