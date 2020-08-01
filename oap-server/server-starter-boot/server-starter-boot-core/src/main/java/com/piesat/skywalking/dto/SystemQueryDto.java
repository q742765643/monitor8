package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SystemQueryDto {
    @ApiModelProperty(value = "设备ip")
    private String ip;
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
