package com.touchbiz.chatgpt.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchbiz.cache.starter.IRedisTemplate;
import com.touchbiz.chatgpt.boot.config.JeecgBaseConfig;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.dto.request.SysLoginModel;
import com.touchbiz.chatgpt.dto.response.LoginUser;
import com.touchbiz.chatgpt.infrastructure.constants.CommonConstant;
import com.touchbiz.chatgpt.infrastructure.utils.Md5Util;
import com.touchbiz.chatgpt.infrastructure.utils.PasswordUtil;
import com.touchbiz.chatgpt.infrastructure.utils.TokenUtils;
import com.touchbiz.chatgpt.service.BaseCommonService;
import com.touchbiz.chatgpt.service.ISysUserService;
import com.touchbiz.common.entity.result.Result;
import com.touchbiz.common.utils.security.EncryptedString;
import com.touchbiz.common.utils.security.JwtUtil;
import com.touchbiz.common.utils.text.oConvertUtils;
import com.touchbiz.common.utils.tools.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags="用户登录")
@Slf4j
public class LoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
    private IRedisTemplate redisUtil;

	@Autowired
	BaseCommonService baseCommonService;

	@Autowired
	private JeecgBaseConfig jeecgBaseConfig;

	private final String BASE_CHECK_CODES = "qwertyuipkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";


	@ApiOperation("登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		String captcha = sysLoginModel.getCaptcha();
		if (captcha == null) {
			result.error500("验证码无效");
			return result;
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		// 加入密钥作为混淆，避免简单的拼接，被外部利用，用户自定义该密钥即可
		String origin = lowerCaseCaptcha + sysLoginModel.getCheckKey() + jeecgBaseConfig.getSignatureSecret();
		String realKey = Md5Util.md5Encode(origin, "utf-8");
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		Object checkCode = redisUtil.get(realKey);
		//当进入登录页时，有一定几率出现验证码错误 #1714
		if (checkCode == null || !checkCode.toString().equals(lowerCaseCaptcha)) {
			log.warn("验证码错误，key= {} , Ui checkCode= {}, Redis checkCode = {}", sysLoginModel.getCheckKey(), lowerCaseCaptcha, checkCode);
			result.error500("验证码错误");
			// 改成特殊的code 便于前端判断
			result.setCode(HttpStatus.PRECONDITION_FAILED.value());
			return result;
		}
		//验证码验证通过，删除缓存
		redisUtil.del(realKey);
		//update-end-author:taoyan date:20190828 for:校验验证码
		
		//1. 校验用户是否有效
		//update-begin-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getUsername,username);
		SysUser sysUser = sysUserService.getOne(queryWrapper);
		//update-end-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}
				
		//用户登录信息
		userInfo(sysUser, result);
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUser, loginUser);

		//TODO 保存用户信息到redis中
		String token = result.getResult().getString("token");

		redisUtil.set(CommonConstant.SYS_USERS_CACHE + token, JsonUtils.toJson(loginUser));
		redisUtil.expire(CommonConstant.SYS_USERS_CACHE + token, JwtUtil.EXPIRE_TIME / 1000);

    	String redisKey= String.format("%s::%s", CommonConstant.SYS_USERS_CACHE, sysUser.getUsername());
    	redisUtil.set(redisKey, loginUser);
    	redisUtil.expire(redisKey, JwtUtil.EXPIRE_TIME / 1000);

    	baseCommonService.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null,loginUser);
        //update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
		return result;
	}


	
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Result<Object> logout(HttpServletRequest request,HttpServletResponse response) {
		//用户退出逻辑
	    String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
	    if(oConvertUtils.isEmpty(token)) {
	    	return Result.error("退出登录失败！");
	    }
	    String username = JwtUtil.getUsername(token);
		var sysUser = sysUserService.getUserByName(username);
	    if(sysUser!=null) {
			LoginUser loginUser = new LoginUser();
			BeanUtils.copyProperties(sysUser, loginUser);
			//update-begin--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
			baseCommonService.addLog("用户名: "+sysUser.getRealname()+",退出成功！", CommonConstant.LOG_TYPE_1, null, loginUser);
			//update-end--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
			log.info(" 用户名:  "+sysUser.getRealname()+",退出成功！ ");
			//清空用户登录Token缓存
			redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
			//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CommonConstant.SYS_USERS_CACHE, sysUser.getUsername()));

			return Result.ok("退出登录成功！");
	    } else {
	    	return Result.error("Token无效!");
	    }
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
		// 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME / 1000);
		obj.put("token", token);

		obj.put("userInfo", sysUser);

		result.setResult(obj);
		result.success("登录成功");
		return result;
	}


	/**
	 * 获取加密字符串
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public Result<Map<String,String>> getEncryptedString(){
		Result<Map<String,String>> result = new Result<>();
		Map<String,String> map = new HashMap(5);
		map.put("key", EncryptedString.key);
		map.put("iv",EncryptedString.iv);
		result.setResult(map);
		return result;
	}

	/**
	 * 后台生成图形验证码 ：有效
	 * @param key
	 */

	/**
	 * 登录二维码
	 */
	@ApiOperation(value = "登录二维码", notes = "登录二维码")
	@GetMapping("/getLoginQrcode")
	public Result<?>  getLoginQrcode() {
		String qrcodeId = CommonConstant.LOGIN_QRCODE_PRE+IdWorker.getIdStr();
		//定义二维码参数
		Map params = new HashMap(5);
		params.put("qrcodeId", qrcodeId);
		//存放二维码唯一标识30秒有效
		redisUtil.set(CommonConstant.LOGIN_QRCODE + qrcodeId, qrcodeId, 30);
		return Result.OK(params);
	}
	/**
	 * 扫码二维码
	 */
	@ApiOperation(value = "扫码登录二维码", notes = "扫码登录二维码")
	@PostMapping("/scanLoginQrcode")
	public Result<?> scanLoginQrcode(@RequestParam String qrcodeId, @RequestParam String token) {
		Object check = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isNotEmpty(check)) {
			//存放token给前台读取
			redisUtil.set(CommonConstant.LOGIN_QRCODE_TOKEN+qrcodeId, token, 60);
		} else {
			return Result.error("二维码已过期,请刷新后重试");
		}
		return Result.OK("扫码成功");
	}


	/**
	 * 获取用户扫码后保存的token
	 */
	@ApiOperation(value = "获取用户扫码后保存的token", notes = "获取用户扫码后保存的token")
	@GetMapping("/getQrcodeToken")
	public Result getQrcodeToken(@RequestParam String qrcodeId) {
		Object token = redisUtil.get(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId);
		Map result = new HashMap(5);
		Object qrcodeIdExpire = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
		if (oConvertUtils.isEmpty(qrcodeIdExpire)) {
			//二维码过期通知前台刷新
			result.put("token", "-2");
			return Result.OK(result);
		}
		if (oConvertUtils.isNotEmpty(token)) {
			result.put("success", true);
			result.put("token", token);
		} else {
			result.put("token", "-1");
		}
		return Result.OK(result);
	}

}