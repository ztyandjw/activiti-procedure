package com.tim.activiti.service;

import com.google.common.collect.Sets;
import com.tim.activiti.exception.ActivitiServiceException;
import com.tim.activiti.util.ActivitiUtils;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/2.
 */

@Service
public class UserTaskServiceImpl {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;


    private  Task checkProcedureIdAndTaskName(String procedureId, String taskName) {
        Task task = taskService.createTaskQuery().processInstanceId(procedureId).singleResult();
        if(task == null) {
            throw new ActivitiServiceException(400, "传入procedureId不正确,task为空");
        }
        if(!StringUtils.equals(taskName, task.getName())) {
            throw new ActivitiServiceException(400, "传入task名称不正确, 正确task名称为 " + task.getName());
        }
        return task;
    }

    public void completeUserTask(String userId, String groupId, Map<String, Object> inputParams, String taskName, String procedureId) {
        Task task = this.checkProcedureIdAndTaskName(procedureId, taskName);
        String taskId = task.getId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procedureId).singleResult();
        String procedureDefinitionKey = processInstance.getProcessDefinitionKey();
        UserTask userTask = ActivitiUtils.getFlowElement(procedureDefinitionKey, UserTask.class, "", -999);
        if(userTask == null) {
            throw new ActivitiServiceException(500, "服务器内部错误");
        }
        //说明是第一个userTask任务
        if(userTask.getId().equals(taskName) || userTask.getName().equals(taskName)) {
            String procedureUserId = processInstance.getStartUserId();
            if(!procedureUserId.equals(userId)) {
                throw new ActivitiServiceException(401, String.format("用户id : %s不正确, 必须与流程发起用户: %s 保持一致", userId, procedureUserId));
            }
        }
        userTask = ActivitiUtils.getFlowElement(procedureDefinitionKey, UserTask.class, taskName, -999);
        List<FormProperty> formPropertyList = userTask.getFormProperties();
        Set<String> requiredKeys = formPropertyList.stream().filter(property -> property.isRequired() == true).map(FormProperty :: getId).collect(Collectors.toSet());
        if(requiredKeys.size() > 0) {
            if(inputParams == null) {
                throw new ActivitiServiceException(400, "inputParams不能为空, 缺失字段为: " + StringUtils.join(requiredKeys, ","));
            }
            Set<String> diff = Sets.newHashSet();
            Set<String> inputKeys = inputParams.keySet();
            diff = Sets.difference(requiredKeys, inputKeys);
            if(diff.size() > 0) {
                throw new ActivitiServiceException(400, "inputParams缺失关键字段, 缺失字段为: " + StringUtils.join(diff, ","));
            }
        }
        List<String> candidateUsers = userTask.getCandidateUsers();
        List<String> candidateGroups = userTask.getCandidateGroups();
        //说明是assign模式
        if(candidateUsers.size() == 0 && candidateGroups.size() == 0) {
            if(userTask.getAssignee() == null || userId.equals(userTask.getAssignee())) {
                if(inputParams != null) {
//                    taskService.setAssignee(taskId, userId);
                    taskService.complete(taskId, inputParams);
                }
                else {
//                    taskService.setAssignee(taskId, userId);
                    taskService.complete(taskId);
                }
            }
            else {
                throw new ActivitiServiceException(400, "用户id: " + userId + " 该用户没有执行任务的权限");

            }
        }
        else {
            if(candidateUsers.contains(userId) || (StringUtils.isNotBlank(groupId) && candidateGroups.contains(groupId))) {
                taskService.claim(task.getId(), userId);
                if(inputParams != null) {
//                    taskService.setAssignee(taskId, userId);
                    taskService.complete(taskId, inputParams);
                }
                else {
//                    taskService.setAssignee(taskId, userId);
                    taskService.complete(taskId);
                }
            }
            else {
                throw new ActivitiServiceException(400, "用户id: " + userId + " 该用户没有执行任务的权限");
            }
        }
    }
}
