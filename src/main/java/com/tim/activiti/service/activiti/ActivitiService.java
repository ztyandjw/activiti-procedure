package com.tim.activiti.service.activiti;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/18.
 */

public abstract class ActivitiService {

    private String definitionKey;
    private String bussinessKey;
    private String procedureId;

    protected String getApolloConfigValue(String key, String defaultValue) {
        Config config = ConfigService.getAppConfig(); //config instance is singleton for each namespace and is never null
        String value = config.getProperty(key, defaultValue);
        return value;
    }

    protected void logHead(DelegateExecution execution, Logger logger) {
        definitionKey = execution.getProcessDefinitionId();
        bussinessKey = execution.getProcessInstanceBusinessKey();
        procedureId = execution.getProcessInstanceId();
        logger.info("ActivitiService实现类 {} 开始执行  |||||流程名称 {}, 流程id {}, 业务id {}|||||", this.getClass().getCanonicalName(),
                definitionKey, procedureId, bussinessKey);
    }

    protected void logTailSuccess(DelegateExecution execution, Logger logger) {
        logger.info("ActivitiService实现类 {} 成功  |||||流程名称 {}, 流程id {}, 业务id {}|||||", this.getClass().getCanonicalName(),
                definitionKey, procedureId, bussinessKey);
    }

    protected void logTailFail(DelegateExecution execution, Logger logger) {
        logger.info("ActivitiService实现类 {} 失败  |||||流程名称 {}, 流程id {}, 业务id {}|||||", this.getClass().getCanonicalName(),
                definitionKey, procedureId, bussinessKey);
    }
}
