package com.touchbiz.chatgpt.simple;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.touchbiz.common.utils.tools.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@Slf4j
public class EventStreamTest {

    String token = "";

    @Test
    public void testRetrofit(){

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
        OpenAiService service = new OpenAiService(token);

        var result = service.createCompletion(completionRequest);
        log.info("result:{}", JsonUtils.toJson(result));
    }

    @SneakyThrows
    @Test
    public void testHttp() throws ExecutionException, InterruptedException {

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
}
