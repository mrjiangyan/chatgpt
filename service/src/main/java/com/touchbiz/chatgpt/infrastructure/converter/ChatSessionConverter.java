package com.touchbiz.chatgpt.infrastructure.converter;

import com.touchbiz.chatgpt.database.domain.ChatSession;
import com.touchbiz.chatgpt.dto.response.ChatSessionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatSessionConverter {

    ChatSessionConverter INSTANCE = Mappers.getMapper(ChatSessionConverter.class);

    ChatSessionDTO transformOut(ChatSession in);
}
