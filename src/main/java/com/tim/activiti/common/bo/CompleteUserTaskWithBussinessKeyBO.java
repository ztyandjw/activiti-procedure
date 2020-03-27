package com.tim.activiti.common.bo;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@ApiModel("入参(完成任务Bussiness Object)")
@Data
@Accessors(chain = true)
public class CompleteUserTaskWithBussinessKeyBO implements Serializable {

    private static final long serialVersionUID = 4406001328687395438L;

    @ApiModelProperty(value = "bussinessKey", required = true, example = "4e0e57f6-5c3b-11ea-9aeb-7085c2cd4cb8")
    @NotBlank(message = "bussinessKey不能为blank")
    private String bussinessKey;

    @ApiModelProperty(value = "definitionKey", required = true, example = "4e0e57f6-5c3b-11ea-9aeb-7085c2cd4cb8")
    @NotBlank(message = "definitionKey 不能为blank")
    private String definitionKey;

    @ApiModelProperty(value = "任务名称，当前流程所在的任务", required = true, example = "用户申请")
    @NotBlank(message = "taskName任务名称不能为blank")
    private String taskName;

    @ApiModelProperty(value = "该任务的参数", required = false, example = "{\"approval\": \"true\"}")
    private Map<String,Object> inputParams;

//    @ApiModelProperty(value = "申请人用户id", required = true, example = "zhangtianyi")
//    @NotBlank(message = "userId不能为blank,后期通过token传值")
//    private String userId;
//
//    @ApiModelProperty(value = "申请人组id", required = true, example = "admin")
//    private String groupId;

    @ApiModelProperty(value = "jwttoken", required = true, example = "jwttoken")
    @NotBlank(message = "token不能为blank")
    private String token;

}


