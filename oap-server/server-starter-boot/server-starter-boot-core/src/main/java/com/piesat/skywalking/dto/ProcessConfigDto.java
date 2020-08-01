package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcessConfigDto {
    @ApiModelProperty(value = "进程所在服务器ip")
    private String ip;
    @ApiModelProperty(value = "进程唯一标识名称")
    private String processName;
}
