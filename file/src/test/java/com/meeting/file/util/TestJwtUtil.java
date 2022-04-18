package com.meeting.file.util;

import com.meeting.common.entity.User;
import com.meeting.common.util.JwtTokenUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwtUtil {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void testCreateToken() {
        User user = new User();
        user.setUsername("user");
        user.setEmail("201900301041@mail.sdu.edu.cn");
        user.setId(1L);
        user.setProfile("");
        String token = jwtTokenUtil.generateToken(user);
        System.out.println(token);
    }

    @Test
    public void testValidateToken() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOm51bGwsImlzcyI6InNkdS1tZWV0aW5nIiwiaWQiOjEsImV4cCI6MTY1MDY3NjY2MSwiaWF0IjoxNjUwMDcxODYxLCJlbWFpbCI6IjIwMTkwMDMwMTA0MUBtYWlsLnNkdS5lZHUuY24iLCJ1c2VybmFtZSI6InVzZXIifQ.jCOU-1HZFZGpTrYKbeAft4rCZ90Rv1iW9DtJ9UnHYrQ";
        System.out.println(jwtTokenUtil.validateToken(token));
    }

}
