package com.touchbiz.chatgpt.controller;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionResult;
import com.touchbiz.chatgpt.dto.ChatResult;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class SSEController {

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<CompletionResult>> sse() {
        return Flux.range(1, 10)
                .map(sequence ->
                {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    CompletionResult result = new CompletionResult();

                    result.setChoices(new ArrayList<>());
                    var completion = new CompletionChoice();
                    completion.setText(UUID.randomUUID().toString() + "\n\n" + sequence + "\n\n");
                    result.getChoices().add(completion);
                    return ServerSentEvent.builder(result).build();
                });
    }
}