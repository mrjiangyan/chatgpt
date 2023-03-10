package com.touchbiz.chatgpt.infrastructure.converter;


import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.response.LoginUser;
import com.touchbiz.chatgpt.dto.response.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    LoginUser transformOut(SysUser user);

    SysUser transformIn(LoginUser user);

    UserDTO transformOut(LoginUser user);


}