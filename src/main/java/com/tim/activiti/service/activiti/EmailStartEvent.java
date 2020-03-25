package com.tim.activiti.service.activiti;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/19.
 */
@Slf4j
public class EmailStartEvent extends  ActivitiService implements ExecutionListener {


    @Override
    public void notify(DelegateExecution execution) {
        logHead(execution, log, " 发送邮件地址: " + execution.getVariable("emailAddress"));
    }
}
