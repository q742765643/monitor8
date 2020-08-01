package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConditionDto {
    //private String relation;
    @ApiModelProperty(value = "操作符号")
    private String paramname;
    @ApiModelProperty(value = "拼接符合 and or")
    private String operate;
    @ApiModelProperty(value = "参数值")
    private String paramvalue;
}
