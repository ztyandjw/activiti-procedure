package com.tim.activiti.service;

import com.google.common.collect.Lists;
import com.tim.activiti.common.dto.ProceduresDTO;
import com.tim.activiti.common.dto.FetchProceduresByUserOrGroupDTO;
import com.tim.activiti.exception.ActivitiServiceException;
import com.tim.activiti.util.ActivitiUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private TaskService taskService;

    private Task valid(String definitionKey, String bussinessKey) {
        if(historyService.createHistoricProcessInstanceQuery().processDefinitionKey(definitionKey).processInstanceBusinessKey(bussinessKey).singleResult() == null) {
            throw new ActivitiServiceException(500, "definitionKey: " + definitionKey + " bussinessKey: " + bussinessKey + " 不存在");
        }
        if(historyService.createHistoricProcessInstanceQuery().processDefinitionKey(definitionKey).processInstanceBusinessKey(bussinessKey).singleResult().getDeleteReason() != null ) {
            throw new ActivitiServiceException(500, "definitionKey: " + definitionKey + " bussinessKey: " + bussinessKey + " 已经被删除");
        }
        Task task = taskService.createTaskQuery().processDefinitionKey(definitionKey).processInstanceBusinessKey(bussinessKey).singleResult();
        if(task == null) {
            throw new ActivitiServiceException(500, "definitionKey: " + definitionKey + " bussinessKey: " + bussinessKey + " 无法更新，当前任务不是用户任务");
        }
        return task;
    }


    public void updateVars(String definitionKey, String bussinessKey, Map<String,Object>updateParams) {
        runtimeService.setVariablesLocal(valid(definitionKey, bussinessKey).getExecutionId(), updateParams);
    }

    public void deleteProcedure(String definitionKey, String bussinessKey, String deleteReason) {
        String procedureId = valid(definitionKey, bussinessKey).getProcessInstanceId();
        runtimeService.deleteProcessInstance(procedureId, deleteReason);
    }


    //启动流程实例
    public String startProcedure(String userId, String definitionKey, String bussinessKey) {
//        identityService.setAuthenticatedUserId(userId);
        //设置startUserId
        Authentication.setAuthenticatedUserId(userId);
        String id;
        ProcessInstance processInstance;
        if(StringUtils.isNotBlank(bussinessKey)) {
            if(historyService.createHistoricProcessInstanceQuery().processDefinitionKey(definitionKey).processInstanceBusinessKey(bussinessKey).list().size() > 0) {
                throw new ActivitiServiceException(500, "definitionKey: " + definitionKey + " bussinessKey: " + bussinessKey + " 已经存在了");
            }
            processInstance = runtimeService.startProcessInstanceByKey(definitionKey, bussinessKey);
            id = processInstance.getId();
            log.info("用户id: {} ,发起流程: {}, 业务id: {}", userId, processInstance.getProcessDefinitionId(), bussinessKey);
        }
        else {
            processInstance = runtimeService.startProcessInstanceByKey(definitionKey);
            id = processInstance.getId();
            log.info("用户id: {} ,发起流程: {}", userId, processInstance.getProcessDefinitionId());
        }
        return id;
    }

    private HistoricProcessInstanceQuery filter(String definitionKey, String startUserId, String order) {
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



    private ProceduresDTO getPorcedureDTO(String taskAssignee, String taskName, HistoricProcessInstance historicProcessInstance) {
        List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();
        ProceduresDTO proceduresDTO = new ProceduresDTO();
        proceduresDTO.wrap(varInstanceList, historicProcessInstance);
        proceduresDTO.setTaskAssignee(taskAssignee);
        proceduresDTO.setTaskName(taskName);
        return proceduresDTO;
    }



    private  List<ProceduresDTO> fetchProceduresByTaskNameNotBlank(List<HistoricProcessInstance> historicProcessInstances, String definitionKey, String taskName, String userId, String groupId) {
        List<ProceduresDTO> procedures = Lists.newArrayList();

        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            //若为null, 说明任务没有被执行完成也没有马上要被执行
            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).taskName(taskName).singleResult();
            if(historicTaskInstance == null) {
                continue;
            }
            int version = historicProcessInstance.getProcessDefinitionVersion();
            UserTask userTask = ActivitiUtils.getFlowElement(definitionKey, UserTask.class, taskName, version);
            String assignee = userTask.getAssignee();
            List<String> candidateGroups = userTask.getCandidateGroups();
            List<String> candidateUsers = userTask.getCandidateUsers();
            if(assignee != null && userId.equals(assignee)) {
                procedures.add(getPorcedureDTO(historicTaskInstance.getAssignee(), taskName, historicProcessInstance));
                continue;
            }
            if(candidateUsers.contains(userId)) {
                procedures.add(getPorcedureDTO(historicTaskInstance.getAssignee(), taskName, historicProcessInstance));
                continue;
            }
            if(candidateGroups.contains(groupId)) {
                procedures.add(getPorcedureDTO(historicTaskInstance.getAssignee(), taskName, historicProcessInstance));
                continue;
            }
        }
        return procedures;
    }

    public List<ProceduresDTO> fetchProcedures(String definitionKey, String startUserId, String order, String taskName, String taskUserId, String taskGroupId) {
        List<ProceduresDTO> procedures = Lists.newArrayList();
        List<HistoricProcessInstance> historicProcessInstances = this.filter(definitionKey, startUserId, order).list();
        if(StringUtils.isNotBlank(taskName)) {
            return fetchProceduresByTaskNameNotBlank(historicProcessInstances, definitionKey, taskName, taskUserId, taskGroupId);
        }
        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            procedures.add(getPorcedureDTO(null, null, historicProcessInstance));
        }
        return procedures;
    }
}
