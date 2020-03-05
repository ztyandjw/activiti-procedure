package com.tim.activiti.service;

import com.google.common.collect.Lists;
import com.tim.activiti.common.dto.FetchProceduresDTO;
import com.tim.activiti.util.ActivitiUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/2/28.
 */


@Service
@Slf4j
public class ProcedureServiceImpl {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;

    //启动流程实例
    public String startProcedure(String userId, String definitionKey) {
//        identityService.setAuthenticatedUserId(userId);
        //设置startUserId
        Authentication.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(definitionKey);
        String id = processInstance.getId();
        log.info("用户id: {} ,发起流程: {}, 流程编号: {}", userId, definitionKey, id);
        return id;
    }

    private HistoricProcessInstanceQuery getQuery(String definitionKey, String startUserId, String order, Boolean isFinished) {
        HistoricProcessInstanceQuery query = null;
        if(definitionKey != null && StringUtils.isNotBlank(definitionKey.trim())) {
            query = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(definitionKey);
        }
        if(startUserId != null && StringUtils.isNotBlank(startUserId.trim())) {
            query.startedBy(startUserId);
        }
        if(isFinished != null && isFinished== true) {
            query.finished();
        }
        if(StringUtils.isNotBlank(order)) {
            if(order.equals("asc")) {
                query.orderByProcessInstanceStartTime().asc();
            }
            else {
                query.orderByProcessInstanceStartTime().desc();
            }
        }
        else {
            query.orderByProcessInstanceStartTime().desc();
        }
        return query;
    }

    public List<FetchProceduresDTO> fetchProceduresByUserOrGroup(String definitionKey, String startUserId, String order, String userid, String groupid, String taskName) {
        List<FetchProceduresDTO> list = Lists.newArrayList();
        List<HistoricProcessInstance> historicProcessInstances = Lists.newArrayList();
        historicProcessInstances = this.getQuery(definitionKey, startUserId, order, false).list();
        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).taskName(taskName).unfinished().singleResult();
            if(historicProcessInstance == null) {
                continue;
            }
            else {
                UserTask userTask = ActivitiUtils.getFlowElement(definitionKey, UserTask.class, taskName);
                String assignee = userTask.getAssignee();
                List<String> candidateGroups = userTask.getCandidateGroups();
                List<String> candidateUsers = userTask.getCandidateUsers();
                //说明activiti的任务中没有配置权限相关
                if(assignee == null && candidateGroups.size() == 0 && candidateUsers.size() ==0) {
                    List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
                    FetchProceduresDTO procedureInstanceDTO = new FetchProceduresDTO();
                    list.add(this.wrapFetchProceduresDTO(procedureInstanceDTO, varInstanceList, historicProcessInstance));
                }
                if(StringUtils.isNotBlank(groupid) && candidateGroups.contains(groupid)) {
                    List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
                    FetchProceduresDTO procedureInstanceDTO = new FetchProceduresDTO();
                    list.add(this.wrapFetchProceduresDTO(procedureInstanceDTO, varInstanceList, historicProcessInstance));
                }
                if(assignee.equals(userid)) {
                    List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
                    FetchProceduresDTO procedureInstanceDTO = new FetchProceduresDTO();
                    list.add(this.wrapFetchProceduresDTO(procedureInstanceDTO, varInstanceList, historicProcessInstance));
                }
                if(candidateUsers.contains(userid)) {
                    List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
                    FetchProceduresDTO procedureInstanceDTO = new FetchProceduresDTO();
                    list.add(this.wrapFetchProceduresDTO(procedureInstanceDTO, varInstanceList, historicProcessInstance));
                }
                continue;
            }
        }
        return  list;
    }

    private FetchProceduresDTO wrapFetchProceduresDTO(FetchProceduresDTO oldVar, List<HistoricVariableInstance> varInstanceList, HistoricProcessInstance historicProcessInstance) {
        oldVar.setStartTime(historicProcessInstance.getStartTime());
        oldVar.setEndTime(historicProcessInstance.getEndTime());
        oldVar.setDeleteReason(historicProcessInstance.getDeleteReason());
        oldVar.setProcedureInstanceId(historicProcessInstance.getId());
        oldVar.setStartUserId(historicProcessInstance.getStartUserId());
        if(varInstanceList.size() > 0) {
            Map<String, Object> vars = varInstanceList.stream().collect(Collectors.toMap(HistoricVariableInstance:: getVariableName, HistoricVariableInstance:: getValue, (v1, v2) -> v2));
            oldVar.setVars(vars);
        }
        return oldVar;
    }

    public List<FetchProceduresDTO> fetchAllProcedures(String definitionKey, String startUserId, String order, Boolean procedureFinished) {
        List<FetchProceduresDTO> list = Lists.newArrayList();
        List<HistoricProcessInstance> historicProcessInstances = Lists.newArrayList();
        historicProcessInstances = this.getQuery(definitionKey, startUserId, order, procedureFinished).list();
        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
            FetchProceduresDTO procedureInstanceDTO = new FetchProceduresDTO();
            list.add(this.wrapFetchProceduresDTO(procedureInstanceDTO, varInstanceList, historicProcessInstance));
        }
        return list;
    }
}
