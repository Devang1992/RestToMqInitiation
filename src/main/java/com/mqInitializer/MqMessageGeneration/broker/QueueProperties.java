package com.mqInitializer.MqMessageGeneration.broker;

import com.ibm.mq.spring.boot.MQConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConfigurationProperties("app.broker")
@Data
public class QueueProperties {
    private Map<String, MqConfigProperties> configs = new LinkedHashMap<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class MqConfigProperties extends MQConfigurationProperties {
        private String request;
        private String response;
    }
}
