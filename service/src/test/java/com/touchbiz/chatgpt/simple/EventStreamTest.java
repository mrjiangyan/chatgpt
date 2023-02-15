package com.touchbiz.chatgpt.simple;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.theokanning.openai.OpenAiApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EventStreamTest {

    String token = "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";

    @Test
    public void testRetrofit(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        String token = "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";
        OkHttpClient client = (new OkHttpClient.Builder()).addInterceptor(new AuthenticationInterceptor(token))
                .connectionPool(new ConnectionPool(5, 1L, TimeUnit.SECONDS)).readTimeout(10*1000, TimeUnit.MILLISECONDS).build();
        Retrofit retrofit = (new Retrofit.Builder()).baseUrl("https://api.openai.com/").client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        var api = (OpenAiApi)retrofit.create(OpenAiApi.class);
    }

    @Test
    public void testHttp() throws ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://example.com/sse"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenApply(HttpResponse::body).get()
                .forEach(System.out::println);
    }

    public static class AuthenticationInterceptor implements Interceptor {
        private final String token;

        AuthenticationInterceptor(String token) {
            this.token = token;
        }

        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request().newBuilder().header("Authorization", "Bearer " + this.token).build();
            return chain.proceed(request);
        }
    }
}
