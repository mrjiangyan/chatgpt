package com.touchbiz.chatgpt.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompontionsResult {
    private List<Choice> choices;
    private long created;
    private String id;
    private String model;
    private String object;

    @Data
    public static class Choice {
        private List<Content> delta;
        private Long index;
    }

    @Data
    public static class Content {
        private String content;

    }
}
