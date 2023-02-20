package com.touchbiz.chatgpt.infrastructure.converter;

import com.touchbiz.chatgpt.database.domain.ChatSessionInfo;
import com.touchbiz.chatgpt.dto.ChatInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatSessionInfoConverter {

    ChatSessionInfoConverter INSTANCE = Mappers.getMapper(ChatSessionInfoConverter.class);

    ChatSessionInfo transformIn(ChatInfo.ChatSessionInfo in);

    ChatInfo.ChatSessionInfo transformOut(ChatSessionInfo in);
}
