package com.emily.sample.request.controller;

import com.emily.infrastructure.web.response.annotation.ApiResponsePackIgnore;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  Emily
 * @since :  2025/6/5 下午3:45
 */
@RestController
public class SseController {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String clientId) {
        SseEmitter emitter = new SseEmitter(60_000L);
        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));
        return emitter;
    }

    @PostMapping("/push")
    public void pushMessage(@RequestParam String clientId,
                            @RequestBody String data) {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .data(data)
                        .id(UUID.randomUUID().toString()));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}
