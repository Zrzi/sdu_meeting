package com.meeting.file.mapper;

import com.meeting.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    /**
     * 根据用户id查询用户
     * @param id 用户id
     * @return 用户
     */
    User findUserByUid(@Param("id") Long id);

    /**
     * 修改用户头像
     * @param user 用户
     * @return 记录数
     */
    int updateUserProfile(@Param("user") User user);

}
