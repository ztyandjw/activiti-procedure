package com.tim.activiti.controller;

import com.tim.activiti.common.bo.ActivieDeadletterJobBO;
import com.tim.activiti.common.vo.CommonResult;
import com.tim.activiti.service.JobServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/26.
 */

@RequestMapping("/job")
@RestController
public class JobController {

    @Autowired
    JobServiceImpl jobService;


    @PostMapping("/runtime/activeJob")
    public CommonResult<Void> activeDeadLetterJob(@RequestBody @Validated ActivieDeadletterJobBO activieDeadletterJobBO) {
        String procedureId = activieDeadletterJobBO.getProcedureId();
        jobService.activeDeadletterJob(procedureId);
        return CommonResult.success(null);
    }
}
