package com.tim.activiti.common.dto;

import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/4.
 */
@ApiModel("历史流程DTO")
@Data
@Accessors(chain = true)
public class ProceduresDTO implements Serializable {

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
    private String procedureStartUserId;
    @ApiModelProperty("业务id")
    private String bussinessKey;
    private String taskName;
    private String taskAssignee;
    private String currentNodeId;
    private String currentNodeType;

     public void  wrap(List<HistoricVariableInstance> varInstanceList, HistoricProcessInstance historicProcessInstance) {
        startTime = historicProcessInstance.getStartTime();
        endTime = historicProcessInstance.getEndTime();
        startTime = historicProcessInstance.getStartTime();
        deleteReason = historicProcessInstance.getDeleteReason();
        procedureInstanceId = historicProcessInstance.getId();
        procedureStartUserId = historicProcessInstance.getStartUserId();
        if(varInstanceList.size() > 0) {
            Set<String> validDuplicated = Sets.newHashSet();
            Collections.reverse(varInstanceList);
            Set<HistoricVariableInstance> variables =  varInstanceList.stream().filter(n-> {
                if(validDuplicated.contains(n.getVariableName())) {
                    return false;
                }
                else {
                    validDuplicated.add(n.getVariableName());
                    return true;
                }
            }).collect(Collectors.toSet());
            vars = variables.stream().collect(Collectors.toMap(HistoricVariableInstance:: getVariableName, HistoricVariableInstance:: getValue));
//            BinaryOperator<HistoricVariableInstance> bi = BinaryOperator.minBy(comparator);
//            vars = varInstanceList.stream().collect(Collectors.toMap(HistoricVariableInstance:: getVariableName, HistoricVariableInstance:: getValue, (v1, v2) -> v1));
        }
        procedureDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
        bussinessKey = historicProcessInstance.getBusinessKey();
    }
}
