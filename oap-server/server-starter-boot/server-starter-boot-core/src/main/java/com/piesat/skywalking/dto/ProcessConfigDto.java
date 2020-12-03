package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProcessConfigDto extends BaseDto {
    @ApiModelProperty(value = "进程所在服务器ip")
    private String ip;
    @ApiModelProperty(value = "进程唯一标识名称")
    private String processName;
    @ApiModelProperty(value = "主机id")
    private String hostId;
    @ApiModelProperty(value = "设备当前状态 0 一般 1 危险 2故障 3正常")
    private Integer currentStatus = -1;
    @ApiModelProperty(value = "执行命令")
    private String cmdline;
    @ApiModelProperty(value = "进程pid")
    private Integer pid;

    private String processChart;
}
