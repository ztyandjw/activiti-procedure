package com.tim.activiti.common.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/4.
 */

@ApiModel("入参(获取历史流程列表Bussiness Object)")
@Data
@Accessors(chain = true)
public class FetchProceduresBO implements Serializable{
//    @ApiModelProperty(value = "是谁启动该流程", required = false, example = "zhangtianyi")
//    private String startUserId;

//    @ApiModelProperty(value = "流程是否结束", required = false, example = "zhangtianyi")
//    private Boolean procedureFinished;

//    @ApiModelProperty(value = "根据流程发起时间排序", required = false, example = "desc|asc")
//    @Pattern(regexp = "desc|asc", message = "order字段必须为desc或者sac")
//    private String order;

    private Integer pageNum;

    private Integer totalCount;


    @ApiModelProperty(value = "流程定义名称", required = false, example = "cloud")
    private String definitionKey;


    private String bussinessKey;

    private String procedureId;


    @ApiModelProperty(value = "任务执行者用户id", required = false, example = "zhangtianyi")
    private String userId;
    @ApiModelProperty(value = "任务执行者组id", required = false, example = "zhangtianyi")
    private String groupId;
    @ApiModelProperty(value = "任务名称", required = false, example = "zhangtianyi")
    private String taskName;

}
