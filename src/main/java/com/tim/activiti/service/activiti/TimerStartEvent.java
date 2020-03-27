package com.tim.activiti.service.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/18.
 */
@Slf4j
public class TimerStartEvent  extends  ActivitiService implements ExecutionListener {


    @Override
    public void notify(DelegateExecution execution) {

        logHead(execution, log, "定时器等待 " + (String)execution.getVariable("expiredDate") + "推动后续节点");
//        logHead(execution, log);
    }
}