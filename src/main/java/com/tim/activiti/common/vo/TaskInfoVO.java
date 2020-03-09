package com.tim.activiti.common.vo;

import lombok.Data;
import org.activiti.bpmn.model.FormProperty;

import javax.annotation.security.DenyAll;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/9.
 */
@Data
public class TaskInfoVO {

    private List<Property> properties;

    private List<String> authUsers;

    private List<String> authGroups;

    private String taskName;

    private String procedureDefinitionKey;


    public void wrapFormProperties(List<FormProperty> formProperties) {
        properties = new ArrayList<>();
        for(FormProperty formProperty: formProperties) {
            Property property = new Property();
            property.setKey(formProperty.getId());
            property.setRequired(formProperty.isRequired());
            property.setComment(formProperty.getExpression());
            property.setType(formProperty.getType());
            properties.add(property);
        }
    }

    @Data
    static final class Property {
        private String key;
        private String type;
        private Boolean required;
        private String comment;
    }
}
