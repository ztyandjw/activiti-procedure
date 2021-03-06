package com.tim.activiti.common.bo;


//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@ApiModel("入参(启动流程Bussiness Object)")
@Data
@Accessors(chain = true)
public class StartProcedureBO implements Serializable{

    private static final long serialVersionUID = 3664720941493905912L;

    @ApiModelProperty(value = "流程名称", required = true, example = "请假流程")
    @NotBlank(message = "definitionKey流程定义名称不能为blank")
    private String definitionKey;


//    private Map<String, Object> inputParams;

    @ApiModelProperty(value = "是哪个用户完成该任务", required = true, example = "zhangtianyi")
    @NotBlank(message = "applyUserId申请人id不能为blank")
    private String applyUserId;

}
