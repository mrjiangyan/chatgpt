package com.touchbiz.chatgpt.common.proxy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 支持EventStream方式访问的版本
 */
@Slf4j
public class OpenAiEventStreamService extends OpenAiService {

    private final HttpClient client;

    private final ObjectMapper mapper;

    private final OpenAiConfig openAiConfig;

    public OpenAiEventStreamService(OpenAiConfig config, OpenAiApi api) {
        super(api);
        this.openAiConfig = config;
        this.client = createHttpClient();
        this.mapper = getMapper();
    }

    public OpenAiEventStreamService(OpenAiConfig config) {
        super(config.getKey());
        this.openAiConfig = config;
        this.client = createHttpClient();
        this.mapper = getMapper();

    }

    private ObjectMapper getMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(openAiConfig.getTimeout())
                .build();
    }

    @SneakyThrows
    private <T> HttpRequest  generatePostHttpRequest(String path, T data) {
        var json = mapper.writeValueAsString(data);
        log.info("json:{}", json);
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(openAiConfig.getTimeout())
                .header("Authorization", "Bearer " + this.openAiConfig.getKey())
                .header( "Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://api.openai.com/" + path))
                .build();
        return request;
    }

    @SneakyThrows
    private <T> HttpRequest  generateGetHttpRequest(String path) {
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(openAiConfig.getTimeout())
                .header("Authorization", "Bearer " + this.openAiConfig.getKey())
                .header( "Content-Type", "application/json")
                .GET()
                .uri(URI.create("https://api.openai.com/" + path))
                .build();
        return request;
    }

    @SneakyThrows
    public Flux<CompletionResult> createCompletionSteam(CompletionRequest request) {
        var httpRequest = generatePostHttpRequest("", request);
        var stream = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenApply(HttpResponse::body).get()
                .map(result->{
                    try {
                        return mapper.readValue(result, CompletionResult.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return Flux
                .defer(() -> Flux.fromStream(stream))
                .subscribeOn(Schedulers.elastic());
    }

}
