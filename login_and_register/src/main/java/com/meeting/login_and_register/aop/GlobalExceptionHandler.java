package com.meeting.login_and_register.aop;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public Map<String, Object> exceptionHandler(Exception exception) {
        // Todo 日志记录
        System.out.println(exception.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put("code", 400);
        map.put("message", exception.getMessage());
        return map;
    }

}
