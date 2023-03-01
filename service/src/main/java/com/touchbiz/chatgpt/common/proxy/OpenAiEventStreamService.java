package com.touchbiz.chatgpt.common.proxy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.touchbiz.chatgpt.boot.config.OpenAiConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotNull;

/**
 * 支持EventStream方式访问的版本
 */
@Slf4j
public class OpenAiEventStreamService extends OpenAiService {

    private final ObjectMapper mapper;

    private final OpenAiConfig openAiConfig;

    public OpenAiEventStreamService(OpenAiConfig config, OpenAiApi api) {
        super(api);
        this.openAiConfig = config;
        this.mapper = getMapper();
    }

    public OpenAiEventStreamService(OpenAiConfig config) {
        super(config.getKey());
        this.openAiConfig = config;
        this.mapper = getMapper();

    }

    public ObjectMapper getMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

    @SneakyThrows
    public Flux<ServerSentEvent<String>> createCompletionFlux(@NotNull CompletionRequest request) {
        request.setStream(true);
        WebClient client = WebClient.create("https://api.openai.com/v1/completions");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<>() {
        };
        return client
                .post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiConfig.getKey())
                .body(BodyInserters.fromValue(mapper.writeValueAsString(request)))
                .retrieve()
                .bodyToFlux(type);
    }

}
