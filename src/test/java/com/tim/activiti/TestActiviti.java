package com.tim.activiti;

import com.tim.activiti.service.ProcedureServiceImpl;
import com.tim.activiti.util.ActivitiUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcedureServiceImpl procedureService;


//    @Autowired
//    private JwtAccessTokenConverter jwtAccessTokenConverter;


    //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0NzEyNzEyMy03Y2I3LTQ1MDYtYWIxZi0wZWUxMzQ3YzFlYzQiLCJ1c2VySWQiOiLlvKDkuIkiLCJncm91cElkIjoi57O757uf566h55CG5ZGY57uEIiwiZXhwIjoxNTg1MDk5MDE1fQ.fYXaKc5qUlC1L_5Of-7NA-sn7Af154ZZVlzzEOxWH1U
    @Test
    public void testJwt() throws  Exception {
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String key = "IGZFZIGkYcWbSDBklck56AT5MmP5hyun";
        byte[] bytes;
        bytes = key.getBytes("UTF-8");
        String abc =  base64Encoder.encode(bytes);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        ZoneId myZone = ZoneId.of("Asia/Shanghai");
        LocalDateTime now = LocalDateTime.now(myZone);
        LocalDateTime expireAccessLocalDate = now.plusSeconds(60000);
        Date expireAccess =  Date.from(expireAccessLocalDate.atZone(myZone).toInstant());
        String accessJti = UUID.randomUUID().toString();
        JwtBuilder builder = Jwts.builder()
                .setId(accessJti)
                .setHeaderParam("typ", "JWT")
                .signWith(signatureAlgorithm, abc)
                .claim("userId", "张三")
                .claim("groupId", "系统管理员组");
        builder.setExpiration(expireAccess);
        String accessToken = builder.compact();
        System.out.println(accessToken);
        System.out.println("`12");
    }

    @Test
    public void setVars() {
        try {
            Map<String, Object> m = new HashMap<>();
            m.put("emailAddress", "fuckyou");
            procedureService.updateVars("superapi", "15", m);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("here");



    }

    @Test
    public void test1() {
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().
                processDefinitionKey("clouda").orderByProcessInstanceStartTime().desc().list();
        for(HistoricProcessInstance historicProcessInstance: historicProcessInstances) {
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).taskName("审批").list();

            if(historicTaskInstances.size() > 0) {
                UserTask userTask = ActivitiUtils.getFlowElement("clouda", UserTask.class, "审批", -999);
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
