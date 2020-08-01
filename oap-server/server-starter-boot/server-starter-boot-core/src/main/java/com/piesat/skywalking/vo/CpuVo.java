package com.piesat.skywalking.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CpuVo {
    @ApiModelProperty(value = "时间")
    private String timestamp;
    @ApiModelProperty(value = "cpu使用率")
    private double usage;
}
