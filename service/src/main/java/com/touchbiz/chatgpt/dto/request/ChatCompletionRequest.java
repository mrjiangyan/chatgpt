package com.touchbiz.chatgpt.dto.request;

import com.theokanning.openai.completion.CompletionRequest;
import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@Generated
public class ChatCompletionRequest extends CompletionRequest {

    private List<ChatMessageRequest> messages;

    private Double temperature;

}
