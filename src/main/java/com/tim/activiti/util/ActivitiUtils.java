package com.tim.activiti.util;

import com.tim.activiti.exception.ActivitiServiceException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/2.
 */

public class ActivitiUtils {


    private static class SingleTonHoler{
        private static RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
    }
    public static <T> T getFlowElement(String definitionKey, Class<T> clazz, String nodeId) {
        ProcessDefinition processDefinition = SingleTonHoler.repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(definitionKey).orderByProcessDefinitionVersion().desc().list().get(0);
        if(processDefinition == null) {
            throw new ActivitiServiceException(500, String.format("从流程名称: %s 获取流程定义失败,", definitionKey));
        }
        BpmnModel model = SingleTonHoler.repositoryService.getBpmnModel(processDefinition.getId());
        if(model != null) {
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            if(StringUtils.isNotBlank(nodeId)) {
//                for(FlowElement flowElement: flowElements) {
//                    if(flowElement.getName().equals(nodeId) || flowElement.getId().equals(nodeId)) {
//                        System.out.println("1123");
//                    }
//                }
                return  (T) flowElements.stream().filter(n -> n.getClass().isAssignableFrom(clazz) && (n.getId().trim().equals(nodeId) || n.getName().trim().equals(nodeId))
                ).findFirst().orElseThrow( () -> new ActivitiServiceException(500,  String.format("从流程名称: %s中提取节点名称:%s, 类型: %s 失败,无法找到", definitionKey, nodeId, clazz.getCanonicalName())));
            }
            else {
                return  (T) flowElements.stream().filter(n -> n.getClass().isAssignableFrom(clazz)
                ).findFirst().orElseThrow( () -> new ActivitiServiceException(500,  String.format("从流程名称: %s中提取类型: %s 失败,无法找到", definitionKey,  clazz.getCanonicalName())));
            }
        }
        else {
            throw new ActivitiServiceException(500, String.format("从流程名称: %s 获取bpmn模型失败", definitionKey));
        }
    }

    public static void main(String[] args) {
        List<String> a = new ArrayList();
        System.out.println(a.contains(null));
    }
}
