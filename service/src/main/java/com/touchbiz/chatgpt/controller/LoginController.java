package com.touchbiz.chatgpt.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.touchbiz.chatgpt.boot.config.JeecgBaseConfig;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.request.SysLoginModel;
import com.touchbiz.chatgpt.dto.response.LoginUser;
import com.touchbiz.chatgpt.infrastructure.constants.CommonConstant;
import com.touchbiz.chatgpt.infrastructure.converter.UserConverter;
import com.touchbiz.chatgpt.infrastructure.utils.Md5Util;
import com.touchbiz.chatgpt.infrastructure.utils.PasswordUtil;
import com.touchbiz.chatgpt.infrastructure.utils.RandImageUtil;
import com.touchbiz.chatgpt.infrastructure.utils.TokenUtils;
import com.touchbiz.chatgpt.service.BaseCommonService;
import com.touchbiz.chatgpt.service.ISysUserService;
import com.touchbiz.common.entity.result.MonoResult;
import com.touchbiz.common.entity.result.Result;
import com.touchbiz.common.utils.security.JwtUtil;
import com.touchbiz.webflux.starter.filter.ReactiveRequestContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

import static com.touchbiz.common.utils.text.CommonConstant.X_ACCESS_TOKEN;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/api/chatGpt/")
@Api(tags="用户登录")
@Slf4j
public class LoginController extends AbstractBaseController<SysUser, ISysUserService> {

	private final String BASE_CHECK_CODES = "qwertyuipkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

	@Autowired
	private BaseCommonService baseCommonService;

	@Autowired
	private JeecgBaseConfig jeecgBaseConfig;

	@ApiOperation("登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody @Validated SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		String captcha = sysLoginModel.getCaptcha();
		String lowerCaseCaptcha = captcha.toLowerCase();

		String origin = lowerCaseCaptcha + sysLoginModel.getCheckKey() + jeecgBaseConfig.getSignatureSecret();
		String realKey = Md5Util.md5Encode(origin, CharsetUtil.UTF_8);
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		String checkCode = String.valueOf(getRedisTemplate().get(realKey));
		//当进入登录页时，有一定几率出现验证码错误 #1714
		if (lowerCaseCaptcha.equals(checkCode)) {
			log.warn("验证码错误，key= {} , Ui checkCode= {}, Redis checkCode = {}", sysLoginModel.getCheckKey(), lowerCaseCaptcha, checkCode);
			result.error500("验证码错误");
			// 改成特殊的code 便于前端判断
			result.setCode(HttpStatus.PRECONDITION_FAILED.value());
			return result;
		}
		//验证码验证通过，删除缓存
		getRedisTemplate().del(realKey);
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		log.info("sysUser:{}", sysUser);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
		var userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		if (!sysUser.getPassword().equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}
				
		//用户登录信息
		userInfo(sysUser, result);
		LoginUser loginUser = UserConverter.INSTANCE.transformOut(sysUser);

		String token = result.getResult().getString("token");
		getRedisTemplate().setObject(CommonConstant.SYS_USERS_CACHE + token, loginUser, JwtUtil.EXPIRE_TIME / 1000);
		baseCommonService.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null,sysUser);
     	return result;
	}
	
	/**
	 * 退出登录
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Result<Object> logout() {
		//用户退出逻辑
	    var user = getCurrentUser();
	    if( user!=null) {
			log.info(" 用户名:  "+ user.getRealname()+",退出成功！ ");
			baseCommonService.addLog("用户名: "+user.getRealname()+",退出成功！", CommonConstant.LOG_TYPE_1, null,
					user);
			var request = ReactiveRequestContextHolder.get();
			String token = request.getHeaders().getFirst(X_ACCESS_TOKEN);
			//清空用户登录Token缓存
			getRedisTemplate().del(CommonConstant.SYS_USERS_CACHE + token);

	    }
		return Result.ok("退出登录成功！");
	}

	/**
	 * 用户信息
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
		String username = sysUser.getUsername();
		String syspassword = sysUser.getPassword();
		// 获取用户部门信息
		JSONObject obj = new JSONObject(new LinkedHashMap<>());
		// 生成token
		String token = TokenUtils.sign(username, syspassword);
		obj.put("token", token);
		obj.put("userInfo", sysUser);
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	/**
	 * 后台生成图形验证码 ：有效
	 * @param key
	 */
	@ApiOperation("获取验证码")
	@GetMapping(value = "/randomImage/{key}")
	public MonoResult randomImage(@PathVariable("key") String key){
		try {
			//生成验证码
			String code = RandomUtil.randomString(BASE_CHECK_CODES,4);
			//存到redis中
			String lowerCaseCode = code.toLowerCase();

			//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
			// 加入密钥作为混淆，避免简单的拼接，被外部利用，用户自定义该密钥即可
			String origin = lowerCaseCode+key+jeecgBaseConfig.getSignatureSecret();
			String realKey = Md5Util.md5Encode(origin, CharsetUtil.UTF_8);
			getRedisTemplate().set(realKey, lowerCaseCode, 60);
			log.info("获取验证码，Redis key = {}，checkCode = {}", realKey, code);
			//返回前端
			Object base64 = RandImageUtil.generate(code);
			return MonoResult.ok(base64);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			MonoResult.ok();
			return MonoResult.error500("获取验证码失败,请检查redis配置!");
		}
	}
}