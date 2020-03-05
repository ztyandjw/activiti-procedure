package com.tim.activiti.common.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/5.
 */

@Data
@Accessors(chain = true)
//获取xx用户或者xx用户组针对于某个任务有权限执行的流程
public class FetchProceduresByUserOrGroupBO implements Serializable {
    private static final long serialVersionUID = -8698694019245729742L;

    @ApiModelProperty(value = "任务执行者用户id", required = false, example = "zhangtianyi")
    @NotBlank(message = "candidateOrAssigneeUserId不能为blank")
    private String candidateOrAssigneeUserId;
    @ApiModelProperty(value = "任务执行者组id", required = false, example = "zhangtianyi")
    private String candidateGroupId;
    @ApiModelProperty(value = "任务名称", required = false, example = "zhangtianyi")
    @NotBlank(message = "taskName不能为blank")
    private String taskName;

    @ApiModelProperty(value = "是谁启动该流程", required = false, example = "zhangtianyi")
    private String startUserId;

    @ApiModelProperty(value = "根据流程发起时间排序", required = false, example = "desc|asc")
    @Pattern(regexp = "desc|asc", message = "order字段必须为desc或者sac")
    private String order;

    @ApiModelProperty(value = "流程定义名称", required = false, example = "cloud")
    private String definitionKey;
}
