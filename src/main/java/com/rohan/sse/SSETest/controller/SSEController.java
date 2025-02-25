package com.rohan.sse.SSETest.controller;

import com.rohan.sse.SSETest.service.SSEService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Log4j2
public class SSEController {
    @Autowired
    private SSEService sseService;

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents(@RequestParam(name = "clientID") String clientID) {
        return sseService.handler(clientID);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/publish")
    public String publish(@RequestParam(name = "clientID") String clientID) {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = sdf.format(new Date(timestamp));
        sseService.publishToEmitter(clientID, "Hello at timestamp " + formattedTime);
        return "Published to emitter: " + clientID + " at: " + formattedTime;
    }

}