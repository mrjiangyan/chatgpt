package com.touchbiz.chatgpt.database.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.touchbiz.chatgpt.database.domain.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
	/**
	  * 通过用户账号查询用户信息
	 * @param username
	 * @return
	 */
    SysUser getUserByName(@Param("username") String username);


	/**
	 * 查询被逻辑删除的用户
     * @param wrapper
     * @return List<SysUser>
	 */
	List<SysUser> selectLogicDeleted(@Param(Constants.WRAPPER) Wrapper<SysUser> wrapper);

	/**
	 * 还原被逻辑删除的用户
     * @param userIds 用户id
     * @param entity
     * @return int
	 */
	int revertLogicDeleted(@Param("userIds") String userIds, @Param("entity") SysUser entity);

	/**
	 * 彻底删除被逻辑删除的用户
     * @param userIds 多个用户id
     * @return int
	 */
	int deleteLogicDeleted(@Param("userIds") String userIds);

    /**
     * 更新空字符串为null【此写法有sql注入风险，禁止随便用】
     * @param fieldName
     * @return int
     */
    @Deprecated
    int updateNullByEmptyString(@Param("fieldName") String fieldName);

}
