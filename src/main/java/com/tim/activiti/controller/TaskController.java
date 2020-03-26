package com.tim.activiti.controller;

import com.tim.activiti.common.bo.CompleteUserTaskBO;
import com.tim.activiti.common.bo.CompleteUserTaskWithBussinessKeyBO;
import com.tim.activiti.common.bo.FetchTaskInfo;
import com.tim.activiti.common.vo.CommonResult;
import com.tim.activiti.common.vo.TaskInfoVO;
import com.tim.activiti.service.UserTaskServiceImpl;
import com.tim.activiti.util.JwtTokenHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/2.
 */


@RequestMapping("/task")
@Api("任务模块")
@RestController
public class TaskController {


    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private UserTaskServiceImpl userTaskService;

    @PostMapping("runtime/completeTask")
    @ApiOperation(value = "执行流程任务")
    public CommonResult<Void> completeCurrentUserTask(@RequestBody @Validated CompleteUserTaskBO completeUserTaskBO) {
        String procedureId = completeUserTaskBO.getProcedureId();
        String taskName = completeUserTaskBO.getTaskName();
        String definitionKey = completeUserTaskBO.getDefinitionKey();
        final Map<String, Object> inputParams = completeUserTaskBO.getInputParams();
        String jwtToken = completeUserTaskBO.getToken();
        String userId = JwtTokenHelper.getUserId(jwtTokenHelper.decode(jwtToken));
        String groupId = JwtTokenHelper.getGroupId(jwtTokenHelper.decode(jwtToken));
        userTaskService.completeUserTask(userId, groupId,  inputParams, taskName, procedureId, definitionKey);
        return CommonResult.success(null);
    }


    @PostMapping("runtime/completeTaskWithBussinessKey")
    @ApiOperation(value = "执行流程任务")
    public CommonResult<Void> completeCurrentUserTaskWithBussinessKey(@RequestBody @Validated CompleteUserTaskWithBussinessKeyBO bo) {
        String definitionKey = bo.getDefinitionKey();
        String bussinessKey = bo.getBussinessKey();
        String taskName = bo.getTaskName();
        final Map<String, Object> inputParams = bo.getInputParams();
        String jwtToken = bo.getToken();
        String userId = JwtTokenHelper.getUserId(jwtTokenHelper.decode(jwtToken));
        String groupId = JwtTokenHelper.getGroupId(jwtTokenHelper.decode(jwtToken));
        userTaskService.completeUserTaskWithBussinessKey(definitionKey, bussinessKey, inputParams, taskName,  userId, groupId);
        return CommonResult.success(null);
    }


    @GetMapping("historic/taskInfo")
    @ApiOperation(value = "获取节点信息")
    public CommonResult<TaskInfoVO> getTaskInfo(@Validated FetchTaskInfo fetchTaskInfo) {
        String taskName = fetchTaskInfo.getTaskName();
        String definitionKey = fetchTaskInfo.getDefinitionKey();
        return CommonResult.success(userTaskService.fetchTaskInfo(definitionKey, taskName));


    }
}
