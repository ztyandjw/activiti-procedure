package com.tim.activiti.exception;


import com.tim.activiti.common.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.BindException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("【全局异常拦截】MethodArgumentNotValidException: 错误信息 {}", e.getMessage());
        String msg = StringUtils.join(e.getBindingResult().getAllErrors().stream().map((x) -> x.getDefaultMessage()).collect(Collectors.toList()), ",");
        return CommonResult.error(400, msg);
    }

    @ResponseBody
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public CommonResult handlePatternException(org.springframework.validation.BindException e) {
        logger.error("【全局异常拦截】org.springframework.validation.BindException: 错误信息 {}", e.getMessage());
        String msg = StringUtils.join(e.getBindingResult().getAllErrors().stream().map((x) -> x.getDefaultMessage()).collect(Collectors.toList()), ",");
        return CommonResult.error(400, msg);
    }

    @ResponseBody
    @ExceptionHandler(value = ActivitiServiceException.class)
    public Object handlerException(ActivitiServiceException e) {

        logger.error("【全局异常拦截】: ActivitiServiceException: 异常信息 {} ", e.getMessage());
//        logger.error("【错误堆栈信息】:", e);
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object handlerException(Exception e) {

        logger.error("【全局异常拦截】: 未捕获异常: 异常信息 {} ", e.getMessage());
        logger.error("【错误堆栈信息】:", e);
        return CommonResult.error(500, e.getMessage());
    }
}
