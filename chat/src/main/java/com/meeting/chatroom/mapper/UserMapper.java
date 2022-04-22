package com.meeting.chatroom.mapper;

import com.meeting.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    /**
     * 根据用户id返回User对象
     * @param id 用户id
     * @return 用户对象
     */
    User findUserById(@Param("id") Long id);

}
