package com.meeting.file.service;

import com.meeting.common.entity.User;
import com.meeting.common.exception.UserExistException;
import com.meeting.file.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserByUid(Long id) {
        User user = userMapper.findUserByUid(id);
        if (user == null) {
            throw new UserExistException("用户不存在");
        }
        return user;
    }

    public boolean updateUserProfile(User user) {
        return userMapper.updateUserProfile(user) != 0;
    }

}
