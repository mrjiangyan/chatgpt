package com.touchbiz.chatgpt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.touchbiz.chatgpt.database.domain.SysUser;
import com.touchbiz.common.entity.result.Result;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询
     * @param username 用户名
     * @return SysUser
     */
	SysUser getUserByName(String username);


	/**
	   * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	Result checkUserIsEffective(SysUser sysUser);

	/**
	 * 保存用户
	 * @param user 用户
	 */
	void saveUser(SysUser user);

	/**
	 * 编辑用户
	 * @param user 用户
	 */
	void editUser(SysUser user);


}
