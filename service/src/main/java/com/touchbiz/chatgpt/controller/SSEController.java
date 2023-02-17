package com.touchbiz.chatgpt.controller;

import com.theokanning.openai.completion.CompletionResult;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class SSEController {

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CompletionResult>> sse() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.builder(new CompletionResult()).build());
    }
}