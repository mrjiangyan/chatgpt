package com.touchbiz.chatgpt.common.proxy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

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

    public ObjectMapper getMapper(){
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
    private <T> HttpRequest  generatePostHttpRequest(T data) {
        var json = mapper.writeValueAsString(data);
        log.info("json:{}", json);
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(openAiConfig.getTimeout())
                .header("Authorization", "Bearer " + this.openAiConfig.getKey())
                .header( "Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://api.openai.com/" + "v1/completions"))
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
    public Stream<CompletionResult> createCompletionSteam(CompletionRequest request) {
        var httpRequest = generatePostHttpRequest(request);
        var stream = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenApply(HttpResponse::body).get()
                .map(result->{
                    try {
                        log.info("result:{}", result);
//                        return mapper.readValue(result, CompletionResult.class);
                        return new CompletionResult();
                    } catch (Exception e) {
                        log.error("e:", e);
                        throw new RuntimeException(e);
                    }
                });
        return stream;
    }

}
