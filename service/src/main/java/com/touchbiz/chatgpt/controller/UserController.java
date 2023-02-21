package com.touchbiz.chatgpt.controller;

import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.response.UserDTO;
import com.touchbiz.chatgpt.infrastructure.converter.UserConverter;
import com.touchbiz.chatgpt.service.ISysUserService;
import com.touchbiz.common.entity.annotation.Auth;
import com.touchbiz.common.entity.model.SysUserCacheInfo;
import com.touchbiz.common.entity.result.MonoResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiangyan
 */
@RestController
@RequestMapping("/api/chatGpt/user/")
@Api(tags="用户信息")
@Slf4j
public class UserController extends AbstractBaseController<SysUser, ISysUserService> {

    /**
     * 获取用户基本信息以及购买的服务信息
     * @return
     */
    @GetMapping
    public MonoResult<UserDTO> info() {
        var user = getCurrentUser();
        //拿到用户是否有购买。
        //购买方式，包年/包月/按次

        //如果是按次则需要显示次数
        var result = UserConverter.INSTANCE.transformOut(user);
        if(result!= null){
            result.setMemberLevel("付费会员");
            result.setSessionCount(0);
        }
        return MonoResult.ok(result);
    }


}
