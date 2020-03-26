package com.tim.activiti.service.activiti;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Arrays;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/18.
 */

public abstract class ActivitiService {


    protected String getApolloConfigValue(String key, String defaultValue) {
        Config config = ConfigService.getAppConfig(); //config instance is singleton for each namespace and is never null
        String value = config.getProperty(key, defaultValue);
        return value;
    }

    protected void logHead(DelegateExecution execution, Logger logger, String ... strs) {


        logger.info(" {} 开始执行  流程名称 {}, 流程id {}, 业务id {},  {}", this.getClass().getCanonicalName(),
                execution.getProcessDefinitionId(), execution.getProcessInstanceId(), execution.getProcessInstanceBusinessKey(), StringUtils.join(strs, ", "));
    }

    protected void logTailSuccess(DelegateExecution execution, Logger logger, String ... strs) {
        logger.info(" {} 成功  流程名称 {}, 流程id {},  业务id {} ,{}", this.getClass().getCanonicalName(),
                execution.getProcessDefinitionId(), execution.getProcessInstanceId(),  execution.getProcessInstanceBusinessKey(), StringUtils.join(strs, ", "));
    }

    protected void logTailFail(DelegateExecution execution, Logger logger, String ... strs) {
        logger.info(" {} 失败   流程名称 {}, 流程id {}, 业务id {}", this.getClass().getCanonicalName(),
                execution.getProcessDefinitionId(), execution.getProcessInstanceId(),
                execution.getProcessDefinitionId(), execution.getProcessInstanceBusinessKey());
    }
}
