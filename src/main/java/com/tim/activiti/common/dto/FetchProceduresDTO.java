package com.tim.activiti.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/4.
 */
@ApiModel("历史流程DTO")
@Data
@Accessors(chain = true)
public class FetchProceduresDTO implements Serializable {

    private static final long serialVersionUID = -7458481085413154476L;

    @ApiModelProperty("流程参数")
    private Map<String,Object> vars;
    @ApiModelProperty("删除原因")
    private String deleteReason;
    @ApiModelProperty("流程发起时间")
    private Date startTime;
    @ApiModelProperty("流程结束时间")
    private Date endTime;
    @ApiModelProperty("流程实例id")
    private String procedureInstanceId;
    @ApiModelProperty("流程定义名称")
    private String procedureDefinitionKey;
    @ApiModelProperty("启动流程用户id")
    private String startUserId;

}
