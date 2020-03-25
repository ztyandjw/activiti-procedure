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
public class DeleteProcedureWithBussinessKeyBO implements Serializable{

    private static final long serialVersionUID = 3664720941493905913L;

    @ApiModelProperty(value = "流程名称", required = true, example = "请假流程")
    @NotBlank(message = "definitionKey流程定义名称不能为blank")
    private String definitionKey;

    @ApiModelProperty(value = "业务id", required = true, example = "请假流程")
    @NotBlank(message = "bussinessKey业务id名称不能为blank")
    private String bussinessKey;

    @ApiModelProperty(value = "jwttoken", required = true, example = "jwttoken")
    @NotBlank(message = "token不能为blank")
    private String token;

//    private Map<String, Object> inputParams;


}
