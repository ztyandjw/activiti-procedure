package com.tim.activiti;

import com.tim.activiti.util.ActivitiUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActiviti {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;


    @Test
    public void test1() {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().
                processDefinitionKey("clouda").orderByProcessInstanceStartTime().desc().list();
        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).taskName("审批").list();

            if(historicTaskInstances.size() > 0) {
                UserTask userTask = ActivitiUtils.getFlowElement("clouda", UserTask.class, "审批");
                List<String> groups = userTask.getCandidateGroups();
                List<String> ids = userTask.getCandidateUsers();
                String assign = userTask.getAssignee();
                System.out.println(groups);
                System.out.println(ids);
                System.out.println(assign);

            }

        }
    }

    @Test
    public void testHistory() {

        //createHistoricProcessInstanceQuery
//        List<HistoricTaskInstance> a1 = historyService.createHistoricTaskInstanceQuery().processDefinitionKey("clouda").list();
        List<HistoricProcessInstance> a2 = historyService.createHistoricProcessInstanceQuery().processDefinitionKey("clouda").list();

//        for(HistoricProcessInstance aa: a2) {
//            List<HistoricTaskInstance> a1 = historyService.createHistoricTaskInstanceQuery().processInstanceId(aa.getId()).list();
//            if(aa.getId().equals("c4f97403-5dce-11ea-9eb3-7085c2cd4cb8")) {
//                System.out.println("xixi");
//
//            }
//
//        }

        for(HistoricProcessInstance bb: a2) {

            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(bb.getId()).list();
            if(bb.getId().equals("7695fe70-5dfa-11ea-a426-7085c2cd4cb8")) {
                System.out.println("xixi");

            }

        }
    }

    @Test
    public void testDeploymentId() {

        ProcessDefinition processDefinition = ProcessEngines.getDefaultProcessEngine().getRepositoryService()//与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery()//创建一个流程定义查询
                .processDefinitionKey("clouda").singleResult();
                        /*指定查询条件,where条件*/
        //.deploymentId(deploymentId)//使用部署对象ID查询
        //.processDefinitionId(processDefinitionId)//使用流程定义ID查询
        //.processDefinitionKey(processDefinitionKey)//使用流程定义的KEY查询
        //.processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

                        /*排序*/

        System.out.println(processDefinition.getId());

        BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());
        if(model != null) {
            Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
            for(FlowElement e : flowElements) {

                System.out.println("flowelement id:" + e.getId() + "  name:" + e.getName() + "   class:" + e.getClass().toString());
            }
        }
    }
}
