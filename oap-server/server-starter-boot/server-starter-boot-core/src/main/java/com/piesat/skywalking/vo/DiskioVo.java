package com.piesat.skywalking.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DiskioVo {
    @ApiModelProperty(value = "时间")
    private String timestamp;
    @ApiModelProperty(value = "磁盘io 写速度 kb/s")
    private double write;
    @ApiModelProperty(value = "磁盘io 读速度 kb/s")
    private double read;
}
