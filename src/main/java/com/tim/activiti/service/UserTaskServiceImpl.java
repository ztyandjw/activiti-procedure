package com.tim.activiti.service;

import com.google.common.collect.Sets;
import com.tim.activiti.common.vo.TaskInfoVO;
import com.tim.activiti.exception.ActivitiServiceException;
import com.tim.activiti.util.ActivitiUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/2.
 */
@Slf4j
@Service
public class UserTaskServiceImpl {

    @Autowired
    private TaskService taskService;


    @Autowired
    private RuntimeService runtimeService;


    public TaskInfoVO fetchTaskInfo(String definitionKey, String taskName) {

        UserTask userTask = ActivitiUtils.getFlowElement(definitionKey, UserTask.class, taskName, -999);

        TaskInfoVO taskInfoVO = new TaskInfoVO();
        taskInfoVO.wrap(userTask.getFormProperties());
        List<String> candidateUsers = userTask.getCandidateUsers();
        List<String> candidateGroups = userTask.getCandidateGroups();
        String assignee = userTask.getAssignee();
        List<String> authUsers = new ArrayList<>();
        if(StringUtils.isNotBlank(assignee)) {
            List<String> users = new ArrayList<>();
            users.add(assignee);
            taskInfoVO.setAuthUsers(users);
            taskInfoVO.setAuthUsers(authUsers);
            return taskInfoVO;
        }
        taskInfoVO.setAuthUsers(candidateUsers);
        taskInfoVO.setAuthGroups(candidateGroups);
        taskInfoVO.setProcedureDefinitionKey(definitionKey);
        taskInfoVO.setTaskName(taskName);
        return taskInfoVO;
    }


    //验证这个procedureId是否能找到当前usertask，并且名称是否一致
    private  Task valid(String procedureId, String taskName) {
        Task task = taskService.createTaskQuery().processInstanceId(procedureId).singleResult();
        if(task == null) {
            throw new ActivitiServiceException(400, "查询不到可供执行的task");
        }
        if(!StringUtils.equals(taskName, task.getName())) {
            throw new ActivitiServiceException(400, "task名称不正确, 正确task名称为 " + task.getName());
        }
        return task;
    }




    public void completeUserTaskWithBussinessKey(String definitionKey, String bussinessKey, Map<String, Object> inputParams, String taskName, String userId, String groupId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionKey(definitionKey).processInstanceBusinessKey(bussinessKey).singleResult();
        if(processInstance == null) {
            throw new ActivitiServiceException(400, "bussinessKey: " + bussinessKey + " definitionKey: " + definitionKey + " 无对应流程");
        }
        String procedureId = processInstance.getId();
        Task task = this.valid(procedureId, taskName);
        String taskId = task.getId();
        UserTask userTask = ActivitiUtils.getFlowElement(definitionKey, UserTask.class, "", -999);
        if(userTask == null) {
            throw new ActivitiServiceException(500, "服务端内部错误");
        }
        //说明是第一个userTask任务
        if(userTask.getId().equals(taskName) || userTask.getName().equals(taskName)) {
            String procedureStartUserId = processInstance.getStartUserId();
            if(!procedureStartUserId.equals(userId)) {
                throw new ActivitiServiceException(401, String.format("用户id : %s不正确, 必须与流程发起用户: %s 保持一致", userId, procedureStartUserId));
            }
        }
        userTask = ActivitiUtils.getFlowElement(definitionKey, UserTask.class, taskName, -999);
        List<FormProperty> formPropertyList = userTask.getFormProperties();
//        Set<String> allKeys = formPropertyList.stream().map(FormProperty :: getId).collect(Collectors.toSet());
        Set<String> requiredKeys = formPropertyList.stream().filter(property -> property.isRequired() == true).map(FormProperty :: getId).collect(Collectors.toSet());
        if(requiredKeys.size() > 0) {
            if(inputParams == null) {
                throw new ActivitiServiceException(400, "inputParams不能为空, 缺失字段为: " + StringUtils.join(requiredKeys, ","));
            }
            Set<String> diff;
            Set<String> inputKeys = inputParams.keySet();
            diff = Sets.difference(requiredKeys, inputKeys);
            if(diff.size() > 0) {
                throw new ActivitiServiceException(400, "inputParams缺失关键字段, 缺失字段为: " + StringUtils.join(diff, ","));
            }
//            diff = Sets.difference(inputKeys, allKeys);
//            if(diff.size() > 0) {
//                throw new ActivitiServiceException(400, ("inputParams多出字段, 字段为:" +  StringUtils.join(diff, ",")));
//            }

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
                log.info("用户id: {} ,完成任务: {}, 流程名称 {}, 流程id{},  业务id: {}", userId, taskName, definitionKey, procedureId,  bussinessKey);

            }
            else {
                throw new ActivitiServiceException(400, "用户id: " + userId + " 没有权限执行任务 " + taskName);
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
                log.info("用户id: {} ,完成任务: {}, 流程名称 {}, 流程id{},  业务id: {}", userId, taskName, definitionKey, procedureId,  bussinessKey);
            }
            else {
                if(StringUtils.isBlank(groupId) && candidateGroups.size() > 0) {
                    throw new ActivitiServiceException(401, "用户id: " + userId + " 无权执行任务: " + taskName + "可能由于groupId为blank");
                }
                throw new ActivitiServiceException(401, "用户id: " + userId + " 无权执行任务: " + taskName);
            }
        }
    }

    public void completeUserTask(String userId, String groupId, Map<String, Object> inputParams, String taskName, String procedureId, String definitionKey) {
        Task task = this.valid(procedureId, taskName);
        String taskId = task.getId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionKey(definitionKey).processInstanceId(procedureId).singleResult();
        String procedureDefinitionKey = processInstance.getProcessDefinitionKey();
        UserTask userTask = ActivitiUtils.getFlowElement(procedureDefinitionKey, UserTask.class, "", -999);
        if(userTask == null) {
            throw new ActivitiServiceException(500, "服务端内部错误");
        }
        //说明是第一个userTask任务
        if(userTask.getId().equals(taskName) || userTask.getName().equals(taskName)) {
            String procedureStartUserId = processInstance.getStartUserId();
            if(!procedureStartUserId.equals(userId)) {
                throw new ActivitiServiceException(401, String.format("用户id : %s不正确, 必须与流程发起用户: %s 保持一致", userId, procedureStartUserId));
            }
        }
        userTask = ActivitiUtils.getFlowElement(procedureDefinitionKey, UserTask.class, taskName, -999);
        List<FormProperty> formPropertyList = userTask.getFormProperties();
        Set<String> requiredKeys = formPropertyList.stream().filter(property -> property.isRequired() == true).map(FormProperty :: getId).collect(Collectors.toSet());
        if(requiredKeys.size() > 0) {
            if(inputParams == null) {
                throw new ActivitiServiceException(400, "inputParams不能为空, 缺失字段为: " + StringUtils.join(requiredKeys, ","));
            }
            Set<String> diff;
            Set<String> inputKeys = inputParams.keySet();
            diff = Sets.difference(requiredKeys, inputKeys);
            if(diff.size() > 0) {
                throw new ActivitiServiceException(400, "inputParams缺失关键字段, 缺失字段为: " + StringUtils.join(diff, ","));
            }
//            diff = Sets.difference(inputKeys, allKeys);
//            if(diff.size() > 0) {
//                throw new ActivitiServiceException(400, ("inputParams多出字段, 字段为:" +  StringUtils.join(diff, ",")));
//            }

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
                log.info("用户id: {} ,完成任务: {}, 流程名称 {}, 流程id{}", userId, taskName, definitionKey, procedureId);
            }
            else {
                throw new ActivitiServiceException(400, "用户id: " + userId + " 没有权限执行任务 " + taskName);

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
                log.info("用户id: {} ,完成任务: {}, 流程名称 {}, 流程id{}", userId, taskName, definitionKey, procedureId);

            }
            else {
                if(StringUtils.isBlank(groupId) && candidateGroups.size() > 0) {
                    throw new ActivitiServiceException(401, "用户id: " + userId + " 无权执行任务: " + taskName + "可能由于groupId为blank");
                }
                throw new ActivitiServiceException(401, "用户id: " + userId + " 无权执行任务: " + taskName);
            }
        }
    }
}
