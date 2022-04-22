package com.meeting.chatroom.mapper;

import com.meeting.chatroom.entity.MessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {

    /**
     * 根据message id返回MessageDO对象
     * @param id message id
     * @return MessageDO对象
     */
    MessageDO findMessageById(@Param("id") Long id);

    /**
     * 根据发送方id返回MessageDO对象
     * @param id 发送方id
     * @return MessageDO对象
     */
    List<MessageDO> findMessageByFromId(@Param("id") Long id);

    /**
     * 根据接收方id返回MessageDO对象
     * @param id 接收方id
     * @return MessageDO对象
     */
    List<MessageDO> findMessageByToId(@Param("id") Long id);

    /**
     * 根据接收方id和status返回MessageDO对象
     * @param id 接收方id
     * @param status 消息状态
     * @return MessageDO对象
     */
    List<MessageDO> findMessageByToIdAndStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据发送方id和接收方id查询好友请求（未回复）
     * @param fromId 发送方id
     * @param toId 接收方id
     * @return message
     */
    MessageDO findRequestByFromIdAndToId(@Param("fromId") Long fromId, @Param("toId") Long toId);

    /**
     * 根据用户id查询还未回复的好友请求的发送方id
     * @param id 用户id
     * @return list 发送方id列表
     */
    List<MessageDO> findRequestByToId(@Param("id") Long id);

    /**
     * 查询历史纪录
     * @param uid1 用户1的id
     * @param uid2 用户2的id
     * @param start 起始缩影
     * @param num 数量
     * @return MessageDO列表
     */
    List<MessageDO> findHistoryMessage(@Param("uid1") Long uid1, @Param("uid2") Long uid2, @Param("start") int start, @Param("num") int num);

    /**
     * 添加消息记录
     * @param message
     * @return
     */
    Integer insertMessage(@Param("message") MessageDO message);

    /**
     * 更新消息记录
     * @param message
     * @return
     */
    Integer updateMessage(@Param("message") MessageDO message);

}
