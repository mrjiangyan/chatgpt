package com.touchbiz.chatgpt.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchbiz.common.utils.tools.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.common.util.encryption.EncryptedString;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysLoginModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.util.RandImageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
    private RedisUtil redisUtil;

	private final String BASE_CHECK_CODES = "qwertyuipkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";


	@ApiOperation("登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		//update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题
		//前端密码加密，后端进行密码解密
		//password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密码解密
		//update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题

		//update-begin-author:taoyan date:20190828 for:校验验证码
		if (isValidation) {
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
		}
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

		redisUtil.set(CacheConstant.SYS_USERS_CACHE + token, JsonUtils.toJson(loginUser));
		redisUtil.expire(CacheConstant.SYS_USERS_CACHE + token, JwtUtil.EXPIRE_TIME / 1000);

    	String redisKey= String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername());
    	redisUtil.set(redisKey, loginUser);
    	redisUtil.expire(redisKey, JwtUtil.EXPIRE_TIME / 1000);

    	baseCommonService.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null,loginUser);
        //update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
		return result;
	}

	@RequestMapping(value = "/getToken", method = RequestMethod.POST)
	public Result<JSONObject> getToken(@RequestBody SysLoginModel sysLoginModel){

		var token = login(sysLoginModel).getResult().getString("token");

		return Result.ok(token);
	}

	/**
	 * 【vue3专用】获取用户信息
	 */
	@GetMapping("/user/getUserInfo")
	public Result<JSONObject> getUserInfo(HttpServletRequest request){
		Result<JSONObject> result = new Result<>();
		String  username = JwtUtil.getUserNameByToken(request);
		if(oConvertUtils.isNotEmpty(username)) {
			// 根据用户名查询用户信息
			SysUser sysUser = sysUserService.getUserByName(username);
			sysUser.setRoles(sysUserService.getRole(username));
			JSONObject obj=new JSONObject();

			//update-begin---author:scott ---date:2022-06-20  for：vue3前端，支持自定义首页-----------
			String version = request.getHeader(CommonConstant.VERSION);

			//update-begin---author:liusq ---date:2022-06-29  for：接口返回值修改，同步修改这里的判断逻辑-----------
			//update-end---author:scott ---date::2022-06-20  for：vue3前端，支持自定义首页--------------
			
			obj.put("userInfo",sysUser);
			result.setResult(obj);
			result.success("");
		}
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
		LoginUser sysUser = sysBaseApi.getUserByName(username);
	    if(sysUser!=null) {
			//update-begin--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
			baseCommonService.addLog("用户名: "+sysUser.getRealname()+",退出成功！", CommonConstant.LOG_TYPE_1, null,sysUser);
			//update-end--Author:wangshuai  Date:20200714  for：登出日志没有记录人员
			log.info(" 用户名:  "+sysUser.getRealname()+",退出成功！ ");
			//清空用户登录Token缓存
			redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
			//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
			//调用shiro的logout
			SecurityUtils.getSubject().logout();
			return Result.ok("退出登录成功！");
	    } else {
	    	return Result.error("Token无效!");
	    }
	}
	
	/**
	 * 获取访问量
	 * @return
	 */
	@GetMapping("loginfo")
	public Result<JSONObject> loginfo() {
		Result<JSONObject> result = new Result<>();
		JSONObject obj = new JSONObject();
		//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		// 获取一天的开始和结束时间
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dayStart = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date dayEnd = calendar.getTime();
		// 获取系统访问记录
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount(dayStart,dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart,dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		obj.put("todayIp", todayIp);
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	

	/**
	 * 短信登录接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/sms")
	public Result<String> sms(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<>();
		String mobile = jsonObject.get("mobile").toString();
		//手机号模式 登录模式: "2"  注册模式: "1"
		String smsmode=jsonObject.get("smsmode").toString();
		log.info(mobile);
		if(oConvertUtils.isEmpty(mobile)){
			result.setMessage("手机号不允许为空！");
			result.setSuccess(false);
			return result;
		}
		
		//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+mobile;
		Object object = redisUtil.get(redisKey);
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		
		if (object != null) {
			result.setMessage("验证码10分钟内，仍然有效！");
			result.setSuccess(false);
			return result;
		}

		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
    	obj.put("code", captcha);
		try {
			boolean b = false;
			//注册模板
			if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				if(sysUser!=null) {
					result.error500(" 手机号已经注册，请直接登录！");
					baseCommonService.addLog("手机号已经注册，请直接登录！", CommonConstant.LOG_TYPE_1, null);
					return result;
				}
				b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
			}else {
				//登录模式，校验用户有效性
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				result = sysUserService.checkUserIsEffective(sysUser);
				if(!result.isSuccess()) {
					String message = result.getMessage();
					String userNotExist="该用户不存在，请注册";
					if(userNotExist.equals(message)){
						result.error500("该用户不存在或未绑定手机号");
					}
					return result;
				}

                if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
					//登录模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
				} else if(CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
					//忘记密码模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
				}
			}

			if (!b) {
				result.setMessage("短信验证码发送失败,请稍后重试");
				result.setSuccess(false);
				return result;
			}
			
			//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
			//验证码10分钟内有效
			redisUtil.set(redisKey, captcha, 600);
			//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
			
			//update-begin--Author:scott  Date:20190812 for：issues#391
			//result.setResult(captcha);
			//update-end--Author:scott  Date:20190812 for：issues#391
			result.setSuccess(true);

		} catch (ClientException e) {
			e.printStackTrace();
			result.error500(" 短信接口未配置，请联系管理员！");
			return result;
		}
		return result;
	}
	

	/**
	 * 手机号登录接口
	 * 
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("手机号登录接口")
	@PostMapping("/phoneLogin")
	public Result<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
		Result<JSONObject> result = new Result<>();
		String phone = jsonObject.getString("mobile");
		
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}
		
		String smscode = jsonObject.getString("captcha");

		//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
		String redisKey = CommonConstant.PHONE_REDIS_KEY_PRE+phone;
		Object code = redisUtil.get(redisKey);
		//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906

		if (!smscode.equals(code)) {
			result.setMessage("手机验证码错误");
			return result;
		}
		//用户信息
		userInfo(sysUser, result);
		//添加日志
		baseCommonService.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);

		return result;
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
		String token = JwtUtil.sign(username, syspassword);
		// 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME / 1000);
		obj.put("token", token);

		// update-begin--Author:sunjianlei Date:20210802 for：获取用户租户信息String tenantIds = sysUser.getRelTenantIds();

		// update-end--Author:sunjianlei Date:20210802 for：获取用户租户信息

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
	@ApiOperation("获取验证码")
	@GetMapping(value = "/randomImage/{key}")
	public Result<String> randomImage(@PathVariable("key") String key){
		Result<String> res = new Result<>();
		try {
			//生成验证码
			String code = RandomUtil.randomString(BASE_CHECK_CODES,4);
			//存到redis中
			String lowerCaseCode = code.toLowerCase();
			
			//update-begin-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
			// 加入密钥作为混淆，避免简单的拼接，被外部利用，用户自定义该密钥即可
			String origin = lowerCaseCode+key+jeecgBaseConfig.getSignatureSecret();
			String realKey = Md5Util.md5Encode(origin, "utf-8");
			//update-end-author:taoyan date:2022-9-13 for: VUEN-2245 【漏洞】发现新漏洞待处理20220906
            
			redisUtil.set(realKey, lowerCaseCode, 60);
			log.info("获取验证码，Redis key = {}，checkCode = {}", realKey, code);
			//返回前端
			String base64 = RandImageUtil.generate(code);
			res.setSuccess(true);
			res.setResult(base64);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			res.error500("获取验证码失败,请检查redis配置!");
			return res;
		}
		return res;
	}

	
	/**
	 * app登录
	 * @param sysLoginModel
	 * @return
   */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) {
		Result<JSONObject> result = new Result<>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		
		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
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
		

		JSONObject obj = new JSONObject();
		//用户登录信息
		obj.put("userInfo", sysUser);
		
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置超时时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME / 1000);

		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		baseCommonService.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 图形验证码
	 * @param sysLoginModel
	 * @return
	 */
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public Result<?> checkCaptcha(@RequestBody SysLoginModel sysLoginModel){
		String captcha = sysLoginModel.getCaptcha();
		String checkKey = sysLoginModel.getCheckKey();
		if(captcha==null){
			return Result.error("验证码无效");
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = Md5Util.md5Encode(lowerCaseCaptcha+checkKey, "utf-8");
		Object checkCode = redisUtil.get(realKey);
		if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
			return Result.error("验证码错误");
		}
		return Result.ok();
	}
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