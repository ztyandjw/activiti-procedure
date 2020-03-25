package com.tim.activiti.common.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/19.
 */

@Data
@Accessors(chain = true)
public class UpdateVarsBO {
    @ApiModelProperty(value = "definitionKey", required = true, example = "definitionKey")
    @NotBlank(message = "definitionKey不能为blank")
    private String definitionKey;
    @ApiModelProperty(value = "bussinessKey", required = true, example = "bussinessKey")
    @NotBlank(message = "bussinessKey不能为blank")
    private String bussinessKey;
    @ApiModelProperty(value = "updateParams", required = true, example = "updateParams")
    @NotEmpty(message = "updateParams不能为empty")
    private Map<String, Object> updateParams;

    @ApiModelProperty(value = "jwttoken", required = true, example = "jwttoken")
    @NotBlank(message = "token不能为blank")
    private String token;

}
