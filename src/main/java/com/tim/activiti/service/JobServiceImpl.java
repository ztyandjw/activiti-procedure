package com.tim.activiti.service;

import org.activiti.engine.ManagementService;
import org.activiti.engine.runtime.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/26.
 */
@Service
public class JobServiceImpl {

    @Autowired
    private ManagementService managementService;

    public void activeDeadletterJob(String procedureId) {
        Job deadLetterJob = managementService.createDeadLetterJobQuery().processInstanceId(procedureId).singleResult();
        managementService.moveDeadLetterJobToExecutableJob(deadLetterJob.getId(), 1);

    }
}
