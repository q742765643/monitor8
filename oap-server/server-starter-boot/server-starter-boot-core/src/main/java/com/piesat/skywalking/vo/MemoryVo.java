package com.piesat.skywalking.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemoryVo {
    @ApiModelProperty(value = "时间")
    private String timestamp;
    @ApiModelProperty(value = "内存使用率")
    private double usage;
}
