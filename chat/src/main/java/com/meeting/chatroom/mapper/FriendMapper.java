package com.meeting.chatroom.mapper;

import com.meeting.chatroom.entity.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FriendMapper {

    /**
     * 根据用户id查询朋友id
     * @param id
     * @return
     */
    List<Long> findFriendsByUserId(@Param("id") Long id);

    /**
     * 添加Friend
     * @param friend
     * @return
     */
    Integer insertFriend(@Param("friend") Friend friend);

    /**
     * 删除朋友
     * @param friend
     * @return
     */
    Integer removeFriend(@Param("friend") Friend friend);

}
