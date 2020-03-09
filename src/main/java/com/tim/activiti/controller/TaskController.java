package com.tim.activiti.controller;

import com.tim.activiti.common.bo.CompleteUserTaskBO;
import com.tim.activiti.common.bo.FetchProceduresByUserOrGroupBO;
import com.tim.activiti.common.bo.FetchTaskInfo;
import com.tim.activiti.common.vo.CommonResult;
import com.tim.activiti.common.vo.TaskInfoVO;
import com.tim.activiti.service.UserTaskServiceImpl;
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
    private UserTaskServiceImpl userTaskService;

    @PostMapping("runtime/completeTask")
    @ApiOperation(value = "执行流程任务")
    public CommonResult<Void> completeCurrentUserTask(@RequestBody @Validated CompleteUserTaskBO completeUserTaskBO) {
        String procedureInstanceId = completeUserTaskBO.getProcedureInstanceId();
        String taskName = completeUserTaskBO.getTaskName();
        final Map<String, Object> inputParams = completeUserTaskBO.getInputParams();
        String userid = completeUserTaskBO.getApplyUserId();
        String groupid = completeUserTaskBO.getApplyUserGroupId();
        userTaskService.completeUserTask(userid, groupid,  inputParams, taskName, procedureInstanceId);
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
