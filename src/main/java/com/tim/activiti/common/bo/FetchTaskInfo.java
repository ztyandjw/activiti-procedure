package com.tim.activiti.common.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/9.
 */
@Data
@Accessors(chain = true)
public class FetchTaskInfo implements Serializable{


    private static final long serialVersionUID = -5663891364631531665L;
    @ApiModelProperty(value = "流程定义名称", required = true, example = "请假流程")
    @NotBlank(message = "definitionKey流程定义名称不能为blank")
    private String definitionKey;
    @ApiModelProperty(value = "任务名称", required = true, example = "审批")
    @NotBlank(message = "taskName任务名称不能为blank")
    private String taskName;


}
