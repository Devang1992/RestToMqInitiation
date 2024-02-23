package com.mqInitializer.MqMessageGeneration.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ResponseEntities {
    private String requestType;
    private String correlationID;
    private String queueName;
}
