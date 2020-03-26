package com.tim.activiti.common.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/26.
 */
@Data
@Accessors(chain = true)
public class ActivieDeadletterJobBO {

    @NotBlank
    private String procedureId;

}
