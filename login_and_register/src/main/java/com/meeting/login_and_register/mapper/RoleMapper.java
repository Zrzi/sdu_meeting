package com.meeting.login_and_register.mapper;

import com.meeting.common.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RoleMapper {

    /**
     * 根据角色id返回角色
     * @param id 角色id
     * @return 角色
     */
    Role findRoleById(@Param("id") Long id);

    /**
     * 根据角色权限返回角色
     * @param authority 权限名
     * @return 角色
     */
    Role findRoleByAuthority(@Param("authority") String authority);

    /**
     * 插入新的角色
     * @param role 角色
     * @return 被影响的记录
     */
    Integer insertRole(@Param("role") Role role);

    /**
     * 更新角色
     * @param role 角色
     * @return 被影响的记录
     */
    Integer updateRole(@Param("role") Role role);

}
