package com.tim.activiti.service.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/18.
 */
@Slf4j
public class TimerEndEvent  extends  ActivitiService implements ExecutionListener {


    @Override
    public void notify(DelegateExecution execution) {
        logTailSuccess(execution, log, "定时器时间已到 " + (String)execution.getVariable("expiredDate") + "推动后续节点");
    }
}
