package com.tim.activiti.service;

import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.tim.activiti.common.dto.FetchAllProceduresDTO;
import com.tim.activiti.common.dto.FetchProceduresByUserOrGroupDTO;
import com.tim.activiti.exception.ActivitiServiceException;
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
import org.springframework.beans.factory.annotation.Value;
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
    public String startProcedure(String userId, String definitionKey, String bussinessKey) {
//        identityService.setAuthenticatedUserId(userId);
        //设置startUserId
        Authentication.setAuthenticatedUserId(userId);
        String id;
        ProcessInstance processInstance;
        if(StringUtils.isNotBlank(bussinessKey)) {
            if(historyService.createHistoricProcessInstanceQuery().processDefinitionKey(definitionKey).processInstanceBusinessKey(bussinessKey).list().size() > 0) {
                throw new ActivitiServiceException(400, "definitionKey: " + definitionKey + " bussinessKey: " + bussinessKey + " 已经存在了");
            }
            processInstance = runtimeService.startProcessInstanceByKey(definitionKey, bussinessKey);
            id = processInstance.getId();
            log.info("用户id: {} ,发起流程: {}, 流程编号: {}, 业务id: {}", userId, definitionKey, id, bussinessKey);
        }
        else {
            processInstance = runtimeService.startProcessInstanceByKey(definitionKey);
            id = processInstance.getId();
            log.info("用户id: {} ,发起流程: {}, 流程编号: {}", userId, definitionKey, id);
        }
        return id;
    }

    private HistoricProcessInstanceQuery getQuery(String definitionKey, String startUserId, String order) {
        HistoricProcessInstanceQuery query = null;
        if(definitionKey != null && StringUtils.isNotBlank(definitionKey.trim())) {
            query = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(definitionKey);
        }
        if(startUserId != null && StringUtils.isNotBlank(startUserId.trim())) {
            query.startedBy(startUserId);
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

    public List<FetchAllProceduresDTO> fetchProceduresByUserOrGroup(String definitionKey, String startUserId, String order, String userid, String groupid, String taskName) {
        List<FetchAllProceduresDTO> list = Lists.newArrayList();
        List<HistoricProcessInstance> historicProcessInstances = Lists.newArrayList();
        historicProcessInstances = this.getQuery(definitionKey, startUserId, order).list();
        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).taskName(taskName).singleResult();
            if(historicTaskInstance == null) {
                continue;

            }
            else {
                int version = historicProcessInstance.getProcessDefinitionVersion();
                UserTask userTask = ActivitiUtils.getFlowElement(definitionKey, UserTask.class, taskName, version);
                String assignee = userTask.getAssignee();
                List<String> candidateGroups = userTask.getCandidateGroups();
                List<String> candidateUsers = userTask.getCandidateUsers();
                if(assignee != null && !userid.equals(assignee)) {
                    continue;
                }
                if((candidateUsers.size() > 0 && !candidateUsers.contains(userid)) && (candidateGroups.size() == 0 || (candidateGroups.size() > 0 && !candidateGroups.contains(groupid)))) {
                    continue;
                }
                if(candidateGroups.size() > 0 && !candidateGroups.contains(groupid)) {
                    continue;
                }
                List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
                FetchProceduresByUserOrGroupDTO fetchProceduresByUserOrGroupDTO = new FetchProceduresByUserOrGroupDTO();
                this.wrapFetchProceduresDTO(fetchProceduresByUserOrGroupDTO, varInstanceList, historicProcessInstance);
                fetchProceduresByUserOrGroupDTO.setTaskAssignee(historicTaskInstance.getAssignee());
                fetchProceduresByUserOrGroupDTO.setTaskName(taskName);
                list.add(fetchProceduresByUserOrGroupDTO);
            }
        }
        return  list;
    }



    private void wrapFetchProceduresDTO(FetchAllProceduresDTO oldVar, List<HistoricVariableInstance> varInstanceList, HistoricProcessInstance historicProcessInstance) {
        oldVar.setStartTime(historicProcessInstance.getStartTime());
        oldVar.setEndTime(historicProcessInstance.getEndTime());
        oldVar.setDeleteReason(historicProcessInstance.getDeleteReason());
        oldVar.setProcedureInstanceId(historicProcessInstance.getId());
        oldVar.setProcedureStartUserId(historicProcessInstance.getStartUserId());
        if(varInstanceList.size() > 0) {
            Map<String, Object> vars = varInstanceList.stream().collect(Collectors.toMap(HistoricVariableInstance:: getVariableName, HistoricVariableInstance:: getValue, (v1, v2) -> v2));
            oldVar.setVars(vars);
        }
        oldVar.setProcedureDefinitionKey(historicProcessInstance.getProcessDefinitionKey());
    }

    public List<FetchAllProceduresDTO> fetchAllProcedures(String definitionKey, String startUserId, String order) {
        List<FetchAllProceduresDTO> list = Lists.newArrayList();
        List<HistoricProcessInstance> historicProcessInstances = Lists.newArrayList();
        historicProcessInstances = this.getQuery(definitionKey, startUserId, order).list();
        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
            FetchAllProceduresDTO procedureInstanceDTO = new FetchAllProceduresDTO();
            this.wrapFetchProceduresDTO(procedureInstanceDTO, varInstanceList, historicProcessInstance);
            list.add(procedureInstanceDTO);
        }
        return list;
    }



}
