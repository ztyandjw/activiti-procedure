package com.tim.activiti.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/5.
 */
@ApiModel("返回值,所有接口统一格式")
public final class CommonResult<T extends  Object> implements Serializable {

    public static Integer CODE_SUCCESS = 0;

    /**
     * 错误码
     */
    @ApiModelProperty(value = "错误码,0为成功,其他为失败,比如400入参不正确,401权限不正确,500服务器内部错误", required = true, example = "0")
    private Integer code;
    /**
     * 错误提示
     */
    @ApiModelProperty(value = "返回信息", required = true, example = "入参不正确")
    private String message;
    /**
     * 返回数据
     */
    @ApiModelProperty(value = "code为0,返回值,数据结构可以是字符串,数字,hash值;若code非0,无data字段", required = true, example = "{\"result\": \"xixi\"}")
    private T data;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     *
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T> 返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMessage());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.isTrue(!CODE_SUCCESS.equals(code), "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = CODE_SUCCESS;
        result.data = data;
        result.message = "";
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return CODE_SUCCESS.equals(code);
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
