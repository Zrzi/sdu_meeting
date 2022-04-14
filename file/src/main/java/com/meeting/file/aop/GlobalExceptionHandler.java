package com.meeting.file.aop;

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
        System.out.println(exception.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("message", "服务器出现异常");
        return map;
    }

}