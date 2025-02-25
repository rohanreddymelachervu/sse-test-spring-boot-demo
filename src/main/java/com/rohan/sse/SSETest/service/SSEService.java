package com.rohan.sse.SSETest.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class SSEService implements MessageListener {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Called when a new SSE connection is established
    public SseEmitter handler(String clientID) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(clientID, emitter);
        emitter.onCompletion(() -> emitters.remove(clientID));
        emitter.onTimeout(() -> emitters.remove(clientID));
        return emitter;
    }

    // Publish an event by sending it to Redis
    public void publishToEmitter(String clientID, String message) {
        // Publish the message in a JSON or delimited format (including the clientID)
        String payload = clientID + ":" + message;
        redisTemplate.convertAndSend("sse-channel", payload);
    }

    // This method is invoked when a message is received from Redis
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        // Assuming payload is in format "clientID:message"
        int index = payload.indexOf(":");
        if (index > 0) {
            String clientID = payload.substring(0, index);
            String eventMessage = payload.substring(index + 1);
            SseEmitter emitter = emitters.get(clientID);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().data(eventMessage));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    emitters.remove(clientID);
                }
            }
        }
    }

    // Optionally, provide a method to retrieve all emitters if needed
    public Collection<SseEmitter> getAllEmitters() {
        return emitters.values();
    }
}


