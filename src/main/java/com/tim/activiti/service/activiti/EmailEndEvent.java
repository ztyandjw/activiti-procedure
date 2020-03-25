package com.tim.activiti.service.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/19.
 */
@Slf4j
public class EmailEndEvent extends  ActivitiService implements ExecutionListener {


    @Override
    public void notify(DelegateExecution execution) {
        logTailSuccess(execution, log);

    }
}
