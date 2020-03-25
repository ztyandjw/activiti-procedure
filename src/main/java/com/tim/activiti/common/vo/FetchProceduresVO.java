package com.tim.activiti.common.vo;

import com.tim.activiti.common.dto.ProceduresDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/5.
 */
@ApiModel("查询历史流程返回值")
@Data
@Accessors(chain = true)
public class FetchProceduresVO implements Serializable {


    private static final long serialVersionUID = -3352100571821452869L;

    private Integer num;

    private List<ProceduresDTO> list;
}
