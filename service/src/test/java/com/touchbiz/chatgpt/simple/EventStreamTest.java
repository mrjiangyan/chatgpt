package com.touchbiz.chatgpt.simple;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class EventStreamTest {


    @Test
    public void testRetrofit(){

        CompletionRequest completionRequest = CompletionRequest.builder()
//                .prompt("Human:" + chat.prompt +"\nAI:")
                .prompt("胡寅恺帅嘛")
                .model("text-davinci-003")
//                .echo(true)
//                .stop(Arrays.asList(" Human:"," AI:"))
                .maxTokens(128)
                .presencePenalty(0d)
                .frequencyPenalty(0d)
                .temperature(0.7D)
                .bestOf(1)
                .topP(1d)
//                .stream(true)
                .build();
        OpenAiService service = new OpenAiService(token);

        var result = service.createCompletion(completionRequest);
        log.info("result:{}", JsonUtils.toJson(result));
    }

    @SneakyThrows
    @Test
    public void testHttp() {

        HttpClient client = HttpClient.newBuilder().build();

        CompletionRequest completionRequest = CompletionRequest.builder()
//                .prompt("Human:" + chat.prompt +"\nAI:")
                .prompt("给我推荐10本小说")
                .model("text-davinci-001")
//                .echo(true)
                .stop(Arrays.asList(" Human:"," AI:"))
                .maxTokens(1024)
                .presencePenalty(0d)
                .frequencyPenalty(0d)
                .temperature(0.7D)
                .bestOf(1)
                .topP(1d)
                .stream(true)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        var json = mapper.writeValueAsString(completionRequest);
        log.info("json:{}", json);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.token)
                .header( "Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://api.openai.com/v1/completions"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenApply(HttpResponse::body).get()
                .forEach(System.out::println);
    }

    @SneakyThrows
    @Test
    public void testFlux(){
        WebClient client = WebClient.create("https://api.openai.com/v1/completions");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<>() {
        };

        CompletionRequest completionRequest = CompletionRequest.builder()
//                .prompt("Human:" + chat.prompt +"\nAI:")
                .prompt("给我推荐10本小说")
                .model("text-davinci-001")
//                .echo(true)
                .stop(Arrays.asList(" Human:"," AI:"))
                .maxTokens(1024)
                .presencePenalty(0d)
                .frequencyPenalty(0d)
                .temperature(0.7D)
                .bestOf(1)
                .topP(1d)
                .stream(true)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        Flux<ServerSentEvent<String>> eventStream = client.post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ")
                .body(BodyInserters.fromValue(mapper.writeValueAsString(completionRequest)))
                .retrieve()
                .bodyToFlux(type);
        eventStream.doOnError(x-> log.error("doOnError SSE:", x));
        eventStream.subscribe(consumer
                ,
                error -> log.error("Error receiving SSE:", error),
                () -> log.info("Completed!!!"));

        Thread.sleep(10*1000);
    }

    private Consumer<ServerSentEvent<String>> consumer = content -> log.info("Time: {} - event: name[{}], id [{}], content[{}] ",
            LocalTime.now(), content.event(), content.id(), content.data());


    @SneakyThrows
    @Test
    public void testModels() {

        HttpClient client = HttpClient.newBuilder().build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.token)
                .header( "Content-Type", "application/json")
                .GET()
                .uri(URI.create("https://api.openai.com/v1/models"))
                .build();

        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).get();
        log.info("response:{}", response);

    }



    @SneakyThrows
    @Test
    public void testChatGptModelHttp() {

        HttpClient client = HttpClient.newBuilder().build();

        List<ChatMessage> message = new ArrayList<>();
        message.add(new ChatMessage("user","请给我推荐10本书"));
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
//                .prompt("Human:" + chat.prompt +"\nAI:")
                .model("gpt-3.5-turbo")
                .stream(true)
                .messages(message).build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        var json = mapper.writeValueAsString(completionRequest);
        log.info("json:{}", json);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + this.token)
                .header( "Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenApply(HttpResponse::body).get()
                .forEach(System.out::println);
    }

    @Builder
    @Data
    public static class ChatCompletionRequest{

        private String model;

        private Boolean stream;

        private List<ChatMessage> messages;
    }

    @AllArgsConstructor
    @Data
    public static class ChatMessage{
        private String role;

        private String content;
    }


}
