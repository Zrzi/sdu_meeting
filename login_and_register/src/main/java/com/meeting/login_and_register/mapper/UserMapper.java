package com.meeting.login_and_register.mapper;

import com.meeting.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    /**
     * 根据用户id返回User对象
     * @param id 用户id
     * @return 用户对象
     */
    User findUserById(@Param("id") Long id);

    /**
     * 根据用户名返回User对象
     * @param username 用户名
     * @return 用户对象
     */
    User findUserByUsername(@Param("username") String username);

    /**
     * 根据用户名返回User对象，包括status=0
     * @param username
     * @return
     */
    User findActiveUserByUsername(@Param("username") String username);

    /**
     * 根据用户邮箱返回User对象
     * @param email 用户邮箱
     * @return 用户对象
     */
    User findUserByEmail(@Param("email") String email);

    /**
     * 根据用户邮箱返回User对象，包括status=0
     * @param email 用户邮箱
     * @return 用户对象
     */
    User findActiveUserByEmail(@Param("email") String email);

    /**
     * 根据验证码返回User对象
     * @param code 验证码
     * @return 用户对象
     */
    User findUserByCode(@Param("code") String code);

    /**
     * 根据用户名查询相似用户
     * @param username 用户名
     * @return 用户对象列表
     */
    List<User> findLikeUserByUsername(@Param("username") String username);

    /**
     * 添加用户记录
     * @param user 用户
     * @return 用户id
     */
    Integer insertUser(@Param("user") User user);

    /**
     * 修改用户信息
     * @param user 用户记录
     * @return 被影响的记录
     */
    Integer updateUser(@Param("user") User user);

}
