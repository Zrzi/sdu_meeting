package com.meeting.login_and_register.service;

import com.meeting.common.entity.Role;
import com.meeting.common.entity.User;
import com.meeting.common.entity.UserRole;
import com.meeting.common.exception.CodeNotFoundException;
import com.meeting.common.exception.IllegalUsernameException;
import com.meeting.common.exception.UserExistException;
import com.meeting.common.util.UUIDUtil;
import com.meeting.login_and_register.mapper.RoleMapper;
import com.meeting.login_and_register.mapper.UserMapper;
import com.meeting.login_and_register.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
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

    @Autowired
    private MailService mailService;

    @Autowired
    private UUIDUtil uuidUtil;

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
        if (username.contains("@")) {
            throw new IllegalUsernameException("用户名不合法");
        }
        if ((userMapper.findUserByUsername(username)) != null) {
            throw new UserExistException("用户名已经存在");
        }
        if ((userMapper.findUserByEmail(email)) != null) {
            throw new UserExistException("邮箱已经存在");
        }
        String code = uuidUtil.getUUID();
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setProfile("default.png");
        user.setCode(code);
        userMapper.insertUser(user);
        userRoleMapper.insertNormalUser(user.getId());
        String subject = "山大云会议";
        // String context = "<a href='http://127.0.0.1:8000/checkCode?code="+code+"'>激活请点击:"+code+"进行激活您的账号</a>";
        String context = "<a href='http://121.40.95.78:8000/checkCode?code="+code+"'>激活请点击:"+code+"进行激活您的账号</a>";
        try {
            mailService.sendHtmlMail(user.getEmail(), subject, context);
        } catch (MessagingException e) {
            throw new RuntimeException("发送邮件时出现异常");
        }
        return user.getId();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void checkCode(String code) {
        User user = userMapper.findUserByCode(code);
        if (user != null) {
            user.setStatus(1);
            user.setCode("");
            userMapper.updateUser(user);
        } else {
            throw new CodeNotFoundException("不存在的验证码");
        }
    }

}
