package com.mqInitializer.MqMessageGeneration.controller;

import com.mqInitializer.MqMessageGeneration.domain.ResponseEntities;
import com.mqInitializer.MqMessageGeneration.service.MqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class MessageInitiationCotroller {
    private final MqService mqService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public ResponseEntities mqMessageInitiator(@RequestHeader HttpHeaders httpHeaders,
                                               @RequestBody String requestBody) {
        return mqService.mqServiceInitiator(httpHeaders, requestBody);
    }
}
