package com.meeting.login_and_register.service;

import com.meeting.common.entity.Role;
import com.meeting.common.entity.User;
import com.meeting.common.entity.UserRole;
import com.meeting.common.exception.UserExistException;
import com.meeting.login_and_register.mapper.RoleMapper;
import com.meeting.login_and_register.mapper.UserMapper;
import com.meeting.login_and_register.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public User loadUserByUsername(String username) {
        User user = userMapper.findUserByUsername(username);
        if (user == null) {
            return user;
        }
        Long uid = user.getId();
        List<Role> authorities = new ArrayList<>();
        List<UserRole> userRoles = userRoleMapper.findUserRolesByUid(uid);
        for (UserRole userRole : userRoles) {
            authorities.add(roleMapper.findRoleById(userRole.getRid()));
        }
        user.setAuthorities(authorities);
        return user;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public User loadUserByEmail(String email) {
        User user = userMapper.findUserByEmail(email);
        if (user == null) {
            return user;
        }
        Long uid = user.getId();
        List<Role> authorities = new ArrayList<>();
        List<UserRole> userRoles = userRoleMapper.findUserRolesByUid(uid);
        for (UserRole userRole : userRoles) {
            authorities.add(roleMapper.findRoleById(userRole.getRid()));
        }
        user.setAuthorities(authorities);
        return user;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Long register(String username, String password, String email) {
        if ((userMapper.findUserByUsername(username)) != null) {
            throw new UserExistException("用户名已经存在");
        }
        if ((userMapper.findUserByEmail(email)) != null) {
            throw new UserExistException("邮箱已经存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setProfile("default.png");
        userMapper.insertUser(user);
        userRoleMapper.insertNormalUser(user.getId());
        return user.getId();
    }

}
