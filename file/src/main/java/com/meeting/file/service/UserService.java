package com.meeting.file.service;

import com.meeting.common.entity.User;
import com.meeting.common.exception.UserExistException;
import com.meeting.file.mapper.UserMapper;
import com.meeting.file.util.PictureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PictureUtil pictureUtil;

    public User findUserByUid(Long id) {
        User user = userMapper.findUserByUid(id);
        if (user == null) {
            throw new UserExistException("用户不存在");
        }
        return user;
    }

    public boolean updateUserProfile(MultipartFile img, User user, String type) throws IOException {
        pictureUtil.handlePicture("user", img, user.getId(), type);
        user.setProfile(type);
        return userMapper.updateUserProfile(user) != 0;
    }

}
