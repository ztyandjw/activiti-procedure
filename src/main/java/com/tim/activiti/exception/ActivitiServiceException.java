package com.tim.activiti.exception;

import java.util.function.Supplier;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/25.
 */

public final class ActivitiServiceException extends RuntimeException  {

    /**
     * 错误码
     */
    private final Integer code;

    public ActivitiServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ActivitiServiceException(Integer code, String message, Throwable e) {
        super(message, e);
        this.code = code;
    }


    public Integer getCode() {
        return code;
    }


}