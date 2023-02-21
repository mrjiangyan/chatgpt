package com.touchbiz.chatgpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatResult {
    private List<Choice> choices;
    private long created;
    private String id;
    private String model;
    private String object;
    private Usage usage;

    @Data
    public static class Choice {
        private String finishReason;
        private Long index;
        private String text;
    }

    @Data
    public static class Usage {
        private long completionTokens;
        private long promptTokens;
        private long totalTokens;
    }
}
