package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ProcessDetailsDto {
    @ApiModelProperty(value = "进程pid")
    private String pid;
    @ApiModelProperty(value = "进程工作目录")
    private String workingDirectory;
    @ApiModelProperty(value = "进程命令行")
    private String cmdline;
    @ApiModelProperty(value = "进程开始时间")
    private String startTime;
    @ApiModelProperty(value = "进程cpu使用率")
    private float cpuUsage;
    @ApiModelProperty(value = "进程cpu总耗时")
    private long cpuTime;
    @ApiModelProperty(value = "进程占用内存字节")
    private long memoryBytes;
    @ApiModelProperty(value = "进程内存占用率")
    private float memoryUsage;
    @ApiModelProperty(value = "进程启动用户")
    private String userName;

}
