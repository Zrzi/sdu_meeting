package com.meeting.login_and_register.service;

import com.meeting.common.entity.Code;
import com.meeting.common.entity.Role;
import com.meeting.common.entity.User;
import com.meeting.common.entity.UserRole;
import com.meeting.common.exception.*;
import com.meeting.common.util.DigitUtil;
import com.meeting.common.util.Md5Util;
import com.meeting.common.util.UUIDUtil;
import com.meeting.login_and_register.mapper.RoleMapper;
import com.meeting.login_and_register.mapper.UserCodeMapper;
import com.meeting.login_and_register.mapper.UserMapper;
import com.meeting.login_and_register.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserCodeMapper userCodeMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private DigitUtil digitUtil;

    @Autowired
    private Md5Util md5Util;

    @Transactional(rollbackFor = RuntimeException.class)
    public User loadUserByUsername(String username) {
        User user = userMapper.findActiveUserByUsername(username);
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
        User user = userMapper.findActiveUserByEmail(email);
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
    public void code(String username, String password, String email) {
        if ((userMapper.findActiveUserByEmail(email)) != null) {
            throw new UserExistException("邮箱已经存在");
        }
        if (username.contains("@")) {
            throw new IllegalUsernameException("用户名不合法");
        }
        User user;
        String code = Integer.toString(digitUtil.code(6));
        if ((user = userMapper.findUserByEmail(email)) != null) {
            // 由于之前判断过，这里只能是未激活的账号，重新发送激活码
            if (!Objects.equals(username, user.getUsername()) &&
                    (userMapper.findUserByUsername(username)) != null) {
                throw new UserExistException("用户名已经存在");
            }
            user.setUsername(username);
            user.setCode(code);
            userMapper.updateUser(user);
        } else {
            if ((userMapper.findUserByUsername(username)) != null) {
                throw new UserExistException("用户名已经存在");
            }
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(md5Util.encrypt(password));
            user.setProfile("");
            user.setCode(code);
            userMapper.insertUser(user);
            userRoleMapper.insertNormalUser(user.getId());
        }
        String subject = "山大云会议";
        String context = "<p>您的验证码为: "+code+"。</p>";
        try {
            mailService.sendHtmlMail(user.getEmail(), subject, context);
        } catch (MessagingException e) {
            throw new RuntimeException("发送邮件时出现异常");
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public Long register(String username, String password, String email, String code) {
        User user = userMapper.findUserByCode(code);
        if (user == null) {
            throw new CodeNotFoundException("不存在的验证码");
        }
        if (!Objects.equals(username, user.getUsername())) {
            throw new UsernameNotFoundException("不存在的用户名");
        }
        if (!Objects.equals(email, user.getEmail())) {
            throw new BaseException("不存在的邮箱");
        }
        user.setPassword(md5Util.encrypt(password));
        user.setStatus(1);
        user.setCode("");
        userMapper.updateUser(user);
        return user.getId();
    }

    /**
     * 发送用户修改密码的验证码
     * @param email 用户邮箱
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void code(String email) {
        User user = userMapper.findActiveUserByEmail(email);
        if (user == null) {
            throw new UserExistException("用户不存在");
        }
        Long uid = user.getId();
        Code userCode = userCodeMapper.findCodeByUserIdAndType(uid, 1);
        String code = Integer.toString(digitUtil.code(6));
        String date = LocalDateTime.now().plusMinutes(10)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (userCode == null) {
            // 记录为空，创建code对象
            userCode = new Code();
            userCode.setId(uid);
            userCode.setType(1);
            userCode.setCode(code);
            userCode.setDate(date);
            userCode.setStatus(0);
            userCodeMapper.insertCode(userCode);
        } else {
            // 记录存在，覆盖记录
            userCode.setCode(code);
            userCode.setDate(date);
            userCode.setStatus(0);
            userCodeMapper.updateCode(userCode);
        }
        String subject = "山大云会议";
        String context = "<p>修改密码的验证码为: "+code+", 验证码有效期为10分钟。</p>";
        try {
            mailService.sendHtmlMail(email, subject, context);
        } catch (MessagingException e) {
            throw new RuntimeException("发送邮件时出现异常");
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void updatePassword(String email, String password, String code) {
        User user = userMapper.findActiveUserByEmail(email);
        if (user == null || user.getStatus() != 1) {
            throw new UserExistException("用户不存在");
        }
        Long uid = user.getId();
        Code userCode = userCodeMapper.findCodeByUserIdAndType(uid, 1);
        if (userCode == null || userCode.getStatus() != 0) {
            throw new CodeNotFoundException("验证码不存在");
        }
        if (!userCode.getCode().equals(code)) {
            throw new CodeNotFoundException("验证码错误");
        }
        if (LocalDateTime.parse(userCode.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isBefore(LocalDateTime.now())) {
            throw new CodeNotFoundException("验证码过期");
        }
        // 验证码不能重复使用
        userCode.setStatus(1);
        userCodeMapper.updateCode(userCode);
        user.setPassword(md5Util.encrypt(password));
        userMapper.updateUser(user);
    }

    public List<User> findUserByName(String username) {
        return userMapper.findLikeUserByUsername(username);
    }

}
