package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : OverviewDto
 * @Description : 预览
 * @Author : zzj
 * @Date: 2020-10-19 18:00
 */
@Data
public class OverviewDto {
    @ApiModelProperty(value = "cpu使用率")
    private float avgCpuPct;
    @ApiModelProperty(value = "内存使用率")
    private float avgMemoryPct;
    @ApiModelProperty(value = "文件使用率")
    private float filesystemPct;
    @ApiModelProperty(value = "磁盘总量GB")
    private float filesystemSize;
    @ApiModelProperty(value = "进程数")
    private float processSize;
    @ApiModelProperty(value = "内存使用GB")
    private float memoryUse;
    @ApiModelProperty(value = "内存总量GB")
    private float memoryTotal;
    @ApiModelProperty(value = "cpu使用核")
    private float cpuUse;
    @ApiModelProperty(value = "cpu总个数")
    private long cpuCores;
    @ApiModelProperty(value = "磁盘使用量GB")
    private float filesystemUse;
    @ApiModelProperty(value = "是否在线 0 不在线 1 在线")
    private Integer online;
    @ApiModelProperty(value = "ip")
    private String ip;
}

