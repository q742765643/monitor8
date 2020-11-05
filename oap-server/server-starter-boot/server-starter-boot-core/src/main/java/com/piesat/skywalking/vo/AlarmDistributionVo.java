package com.piesat.skywalking.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmDistributionVo {
    @ApiModelProperty(value = "分类")
    @JsonProperty("name")
    private String classify;
    @ApiModelProperty(value = "数量")
    @JsonProperty("value")
    private long num;
}
