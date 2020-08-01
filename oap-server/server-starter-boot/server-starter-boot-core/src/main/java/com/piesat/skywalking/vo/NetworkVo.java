package com.piesat.skywalking.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NetworkVo {
    @ApiModelProperty(value = "时间")
    private String timestamp;
    @ApiModelProperty(value = "流量 输入速度 kb/s")
    private double inSpeed;
    @ApiModelProperty(value = "流量 输出速度 kb/s")
    private double outSpeed;
}
