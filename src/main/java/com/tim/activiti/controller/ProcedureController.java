package com.tim.activiti.controller;

import com.tim.activiti.common.bo.*;
import com.tim.activiti.common.dto.ProceduresDTO;
import com.tim.activiti.common.vo.CommonResult;
import com.tim.activiti.common.vo.FetchProceduresVO;
import com.tim.activiti.exception.ActivitiServiceException;
import com.tim.activiti.service.ProcedureServiceImpl;
import com.tim.activiti.util.JwtTokenHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/2/28.
 */

@RequestMapping("/procedure")
@Api("引擎模块")
@RestController
public class ProcedureController {

    @Autowired
    private ProcedureServiceImpl procedureService;


    @Autowired
    private JwtTokenHelper jwtTokenHelper;




    @PostMapping("/runtime/deleteWithBussinessKey")
    @ApiOperation(value = "终止流程实例")
    public CommonResult<Void> deleteProcedure(@RequestBody @Validated DeleteProcedureWithBussinessKeyBO deleteProcedureWithBussinessKeyBO) {
        String token = deleteProcedureWithBussinessKeyBO.getToken();
        JwtTokenHelper.authenticationGroup("系统管理员组", jwtTokenHelper.decode(token));
        String processDefinitionKey = deleteProcedureWithBussinessKeyBO.getDefinitionKey();
        String bussinessKey = deleteProcedureWithBussinessKeyBO.getBussinessKey();
        String reason = "被云平台用户: " + JwtTokenHelper.getUserId(jwtTokenHelper.decode(token)) + " 终止";
//        String deleteReason = deleteProcedureWithBussinessKeyBO.getDeleteReason();
        procedureService.deleteProcedure(processDefinitionKey, bussinessKey, reason);
        return CommonResult.success(null);
    }





    @PostMapping("/runtime/start")
    @ApiOperation(value = "启动流程实例")
    public CommonResult<String> startProcedure(@RequestBody @Validated StartProcedureBO startProcedureBO) {
        //启动id
        String processDefinitionKey =startProcedureBO.getDefinitionKey();
        String jwtToken = startProcedureBO.getToken();
        String userId = JwtTokenHelper.getUserId(jwtTokenHelper.decode(jwtToken));
        return CommonResult.success(procedureService.startProcedure(userId, processDefinitionKey, null));
    }

    @PostMapping("/runtime/startWithBussinessKey")
    @ApiOperation(value = "启动流程实例带上业务id")
    public CommonResult<String> startProcedureWithBussinessKey(@RequestBody @Validated StartProcedureWithBussinessKeyBO startProcedureWithBussinessKeyBO) {
        //启动id
        String processDefinitionKey =startProcedureWithBussinessKeyBO.getDefinitionKey();
        String bussinessKey = startProcedureWithBussinessKeyBO.getBussinessKey();
        String jwtToken = startProcedureWithBussinessKeyBO.getToken();
        String userId = JwtTokenHelper.getUserId(jwtTokenHelper.decode(jwtToken));
        return CommonResult.success(procedureService.startProcedure(userId, processDefinitionKey, bussinessKey));
    }


    @PostMapping("/runtime/updateInputParams")
    @ApiOperation(value = "更新参数")
    public CommonResult<String> updateInputParams(@RequestBody @Validated UpdateVarsBO updateVarsBO) {
        String token = updateVarsBO.getToken();
        JwtTokenHelper.authenticationGroup("系统管理员组", jwtTokenHelper.decode(token));
        String processDefinitionKey =updateVarsBO.getDefinitionKey();
        String bussinessKey = updateVarsBO.getBussinessKey();
        Map<String, Object> updateParams =updateVarsBO.getUpdateParams();
        procedureService.updateVars(processDefinitionKey, bussinessKey, updateParams);
        return CommonResult.success(null);
    }


    @GetMapping("/historic/fetchAll")
    @ApiOperation(value = "获取流程实例集合")
    public CommonResult<FetchProceduresVO> fetchAllProcedures(@Validated FetchProceduresBO fetchProceduresBO) {
        String definitionKey = fetchProceduresBO.getDefinitionKey();
        String procedureId = fetchProceduresBO.getProcedureId();
        String bussinessKey = fetchProceduresBO.getBussinessKey();
        Integer pageNum = fetchProceduresBO.getPageNum();
        String taskName = fetchProceduresBO.getTaskName();
        String userId = fetchProceduresBO.getUserId();
        String groupId = fetchProceduresBO.getGroupId();
        if(StringUtils.isNotBlank(taskName) && StringUtils.isBlank(userId)) {
            throw new ActivitiServiceException(400,  "taskName非空,userId不能为空");
        }
        List<ProceduresDTO> allList = procedureService.fetchAllProcedures(definitionKey, bussinessKey, procedureId, taskName, userId, groupId, pageNum);
        int allSize = allList.size();
        FetchProceduresVO fetchProceduresVO = new FetchProceduresVO();
        List<ProceduresDTO> pagingList = procedureService.fetchPagingProcedures(pageNum, allList);
        fetchProceduresVO.setList(pagingList);
        fetchProceduresVO.setNum(allSize);
        return CommonResult.success(fetchProceduresVO);
    }

//    @GetMapping("/historic/fetchByUserOrGroup")
//    @ApiOperation(value = "获取某用户或某组用户可以审批的流程")
//    public CommonResult<FetchProceduresVO> fetchProceduresByUserOrGroup(@Validated FetchProceduresByUserOrGroupBO fetchProceduresByUserOrGroupBO) {
//        String definitionKey = fetchProceduresByUserOrGroupBO.getDefinitionKey();
//        String startUserId = fetchProceduresByUserOrGroupBO.getStartUserId();
//        String order = fetchProceduresByUserOrGroupBO.getOrder();
//        String  candidateOrAssigneeUserId = fetchProceduresByUserOrGroupBO.getCandidateOrAssigneeUserId();
//        String taskName = fetchProceduresByUserOrGroupBO.getTaskName();
//        String candidateGroupId = fetchProceduresByUserOrGroupBO.getCandidateGroupId();
//        List<ProceduresDTO> list = procedureService.fetchProceduresByUserOrGroup(definitionKey, startUserId, order, candidateOrAssigneeUserId, candidateGroupId, taskName);
//        FetchProceduresVO fetchProceduresVO = new FetchProceduresVO();
//        fetchProceduresVO.setList(list);
//        fetchProceduresVO.setNum(list.size());
//        return CommonResult.success(fetchProceduresVO);
//    }
}
