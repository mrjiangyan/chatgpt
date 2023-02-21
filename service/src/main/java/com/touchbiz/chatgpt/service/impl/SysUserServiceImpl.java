package com.touchbiz.chatgpt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.chatgpt.database.mapper.SysUserMapper;
import com.touchbiz.chatgpt.service.BaseCommonService;
import com.touchbiz.chatgpt.service.ISysUserService;
import com.touchbiz.common.entity.result.Result;
import com.touchbiz.common.utils.text.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @Author: scott
 * @Date: 2018-12-20
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

	@Autowired
	BaseCommonService baseCommonService;


	@Override
	public SysUser getUserByName(String username) {
		return getBaseMapper().getUserByName(username);
	}

	/**
	   * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	@Override
	public Result<?> checkUserIsEffective(SysUser sysUser) {
		Result<?> result = new Result<>();
		//情况1：根据用户信息查询，该用户不存在
		if (sysUser == null) {
			result.error500("该用户不存在，请注册");
			baseCommonService.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
			return result;
		}
		//情况2：根据用户信息查询，该用户已注销
		//update-begin---author:王帅   Date:20200601  for：if条件永远为falsebug------------
		if (CommonConstant.DEL_FLAG_1.equals(sysUser.getDelFlag())) {
		//update-end---author:王帅   Date:20200601  for：if条件永远为falsebug------------
			baseCommonService.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
			result.error500("该用户已注销");
			return result;
		}
		//情况3：根据用户信息查询，该用户已冻结
		if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
			baseCommonService.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
			result.error500("该用户已冻结");
			return result;
		}
		return result;
	}

	@Override
	public void saveUser(SysUser user) {

	}

	@Override
	public void editUser(SysUser user) {

	}

}
