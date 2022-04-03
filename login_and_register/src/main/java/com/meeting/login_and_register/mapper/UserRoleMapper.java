package com.meeting.login_and_register.mapper;

import com.meeting.common.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserRoleMapper {

    /**
     * 根据用户id和权限id查询关联
     * @param uid 用户id
     * @param rid 权限id
     * @return 关联（单个）
     */
    UserRole findUserRoleByUidAndRid(@Param("uid") Long uid, @Param("rid") Long rid);

    /**
     * 根据用户id查询关联
     * @param uid 用户id
     * @return 关联（多个）
     */
    List<UserRole> findUserRolesByUid(@Param("uid") Long uid);

    /**
     * 添加用户权限
     * @param uid 用户id
     * @param rid 角色id
     * @return 被影响的记录
     */
    Integer insertUserRole(@Param("uid") Long uid, @Param("rid") Long rid);

    /**
     * 添加普通用户，即用户角色为user
     * @param uid 用户id
     * @return 被影响的记录
     */
    Integer insertNormalUser(@Param("uid") Long uid);

}
