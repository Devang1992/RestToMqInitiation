package com.mqInitializer.MqMessageGeneration.service;

import com.mqInitializer.MqMessageGeneration.broker.MqInitiator;
import com.mqInitializer.MqMessageGeneration.broker.QueueProperties;
import com.mqInitializer.MqMessageGeneration.domain.ResponseEntities;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqService {
    private final MqInitiator mqinitiator;
    private final QueueProperties queueProperties;
    public static final String CORRELATION_ID_HEADER_NAME = "jms-correlation-id";
    public static final String ENVIRONMENT_HEADER_NAME = "env-id";

    public ResponseEntities mqServiceInitiator(HttpHeaders httpHeaders, String request) {
        String type = request.contains("</") ? "XML" : "JSON";
        String destination = switch (httpHeaders.getFirst(ENVIRONMENT_HEADER_NAME)) {
            case "local" -> queueProperties.getConfigs().get("local").getRequest();
            case null, default -> queueProperties.getConfigs().get("default").getRequest();
        };
        mqinitiator.sendMessage(destination, request,
                httpHeaders.getFirst(CORRELATION_ID_HEADER_NAME));
        return new ResponseEntities(type, httpHeaders.getFirst(CORRELATION_ID_HEADER_NAME), destination);
    }
}
