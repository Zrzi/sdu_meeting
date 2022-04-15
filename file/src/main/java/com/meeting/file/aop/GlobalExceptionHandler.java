package com.meeting.file.aop;

import com.meeting.common.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获MissingServletRequestParameterException异常，400
     * @param exception MissingServletRequestParameterException
     * @return 400响应
     */
    @ResponseBody
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleException(MissingServletRequestParameterException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 400);
        map.put("message", exception.getMessage());
        return map;
    }

    /**
     * 捕获UnAuthorizedException异常，401
     * @param exception UnAuthorizedException
     * @return 401响应
     */
    @ResponseBody
    @ExceptionHandler(value = {UnAuthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleException(UnAuthorizedException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 401);
        map.put("message", exception.getMsg());
        return map;
    }

    /**
     * 捕获NoHandlerFoundException异常，405
     * @param exception NoHandlerFoundException
     * @return 404响应
     */
    @ResponseBody
    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleException(NoHandlerFoundException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 404);
        map.put("message", exception.getMessage());
        return map;
    }

    /**
     * 捕获FileNotFoundException异常，405
     * @param exception FileNotFoundException
     * @return 404响应
     */
    @ResponseBody
    @ExceptionHandler(value = {FileNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleException(FileNotFoundException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 404);
        map.put("message", exception.getMessage());
        return map;
    }

    /**
     * 捕获HttpRequestMethodNotSupportedException异常，405
     * @param exception HttpRequestMethodNotSupportedException
     * @return 405响应
     */
    @ResponseBody
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Map<String, Object> handleException(HttpRequestMethodNotSupportedException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 405);
        map.put("message", exception.getMessage());
        return map;
    }

    /**
     * 捕获MaxUploadSizeExceededException异常，文件大小超出上限
     * @param exception MaxUploadSizeExceededException
     * @return 500响应
     */
    @ResponseBody
    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(MaxUploadSizeExceededException exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("message", exception.getMessage());
        return map;
    }

    /**
     * 捕获其它异常
     * @param exception Exception
     * @return 500响应
     */
    @ResponseBody
    @ExceptionHandler(value = {Exception.class})
    public Map<String, Object> exceptionHandler(Exception exception) {
        System.out.println(exception.getMessage());
        System.out.println(exception.getClass());
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("message", "服务器出现异常");
        return map;
    }

}