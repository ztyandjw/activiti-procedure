package com.tim.activiti.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/5.
 */
@Data
public class FetchProceduresByUserOrGroupDTO extends FetchAllProceduresDTO{


    private static final long serialVersionUID = -783040234016881802L;

    private String taskName;

    private String taskAssignee;

}
