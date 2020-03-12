package com.tim.activiti.controller;

import com.tim.activiti.common.bo.FetchProceduresBO;
import com.tim.activiti.common.bo.FetchProceduresByUserOrGroupBO;
import com.tim.activiti.common.bo.StartProcedureBO;
import com.tim.activiti.common.dto.FetchAllProceduresDTO;
import com.tim.activiti.common.vo.CommonResult;
import com.tim.activiti.common.vo.FetchProceduresVO;
import com.tim.activiti.service.ProcedureServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/2/28.
 */

@RequestMapping("/procedure")
@Api("引擎模块")
@RestController
public class ProcedureController {

    @Autowired
    private ProcedureServiceImpl procedureService;

    @PostMapping("/runtime/start")
    @ApiOperation(value = "启动流程实例")
    public CommonResult<String> startProcedure(@RequestBody @Validated StartProcedureBO startProcedureBO) {
        //启动id
        String processDefinitionKey =startProcedureBO.getDefinitionKey();
        String userId = startProcedureBO.getApplyUserId();
        return CommonResult.success(procedureService.startProcedure(userId, processDefinitionKey));
    }


    @PostMapping("/runtime/updateInputParams")
    @ApiOperation(value = "启动流程实例")
    public CommonResult<String> updateInputParams(@RequestBody @Validated StartProcedureBO startProcedureBO) {
        //启动id
        String processDefinitionKey =startProcedureBO.getDefinitionKey();
        String userId = startProcedureBO.getApplyUserId();
        return CommonResult.success(procedureService.startProcedure(userId, processDefinitionKey));
    }


    @GetMapping("/historic/fetchAll")
    @ApiOperation(value = "获取流程实例集合")
    public CommonResult<FetchProceduresVO> fetchAllProcedures(@Validated FetchProceduresBO fetchProceduresBO) {
        String definitionKey = fetchProceduresBO.getDefinitionKey();
        String startUserId = fetchProceduresBO.getStartUserId();
        String order = fetchProceduresBO.getOrder();
        List<FetchAllProceduresDTO> list = procedureService.fetchAllProcedures(definitionKey, startUserId, order);
        FetchProceduresVO fetchProceduresVO = new FetchProceduresVO();
        fetchProceduresVO.setList(list);
        fetchProceduresVO.setNum(list.size());
        return CommonResult.success(fetchProceduresVO);
    }

    @GetMapping("/historic/fetchByUserOrGroup")
    @ApiOperation(value = "获取某用户或某组用户可以审批的流程")
    public CommonResult<FetchProceduresVO> fetchProceduresByUserOrGroup(@Validated FetchProceduresByUserOrGroupBO fetchProceduresByUserOrGroupBO) {
        String definitionKey = fetchProceduresByUserOrGroupBO.getDefinitionKey();
        String startUserId = fetchProceduresByUserOrGroupBO.getStartUserId();
        String order = fetchProceduresByUserOrGroupBO.getOrder();
        String  candidateOrAssigneeUserId = fetchProceduresByUserOrGroupBO.getCandidateOrAssigneeUserId();
        String taskName = fetchProceduresByUserOrGroupBO.getTaskName();
        String candidateGroupId = fetchProceduresByUserOrGroupBO.getCandidateGroupId();
        List<FetchAllProceduresDTO> list = procedureService.fetchProceduresByUserOrGroup(definitionKey, startUserId, order, candidateOrAssigneeUserId, candidateGroupId, taskName);
        FetchProceduresVO fetchProceduresVO = new FetchProceduresVO();
        fetchProceduresVO.setList(list);
        fetchProceduresVO.setNum(list.size());
        return CommonResult.success(fetchProceduresVO);
    }



}
