package com.emily.sample.request.controller;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author :  Emily
 * @since :  2025/6/5 下午3:45
 */
@RestController
public class SseController {
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String clientId) {
        System.out.println("Start:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(1));
        emitters.put(clientId, emitter);

        //处理正常连接关闭，适合资源清理操作
        emitter.onCompletion(() -> {
            System.out.println("complete:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            emitters.remove(clientId);
        });
        //建议动态调整60秒默认超时，根据业务需求设置1-5分钟
        emitter.onTimeout(() -> {
            System.out.println("timeout:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            emitters.remove(clientId);
        });
        //应添加异常类型判断，区分IO异常与其他业务异常
        emitter.onError(throwable -> {
            System.out.println("Throwable:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            emitters.remove(clientId);
        });
        return emitter;
    }
    @Scheduled(fixedRate = 10000)
    public void schedule(){
        emitters.forEach((clientId, emitter) -> {
            try {
                System.out.println("heartbeat:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "-clientIds:" + emitters.keySet().size());
                emitter.send(SseEmitter.event()
                        .data(clientId + ":heartbeat")
                        .comment("heartbeat")
                        .id(UUID.randomUUID().toString())
                );
            } catch (IOException e) {
                System.out.println("Heartbeat failed for client: {" + clientId + "}");
            }
        });
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
