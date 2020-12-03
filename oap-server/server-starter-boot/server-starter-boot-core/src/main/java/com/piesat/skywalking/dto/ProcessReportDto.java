package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : ProcessReportDto
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-03 10:01
 */
@Data
public class ProcessReportDto {
    @ApiModelProperty(name = "告警名称")
    private long alarmNum;

    @ApiModelProperty(name = "平均cpu使用率")
    private float avgCpuPct;

    @ApiModelProperty(name = "平均内存使用率")
    private float avgMemoryPct;

    @ApiModelProperty(name = "ip")
    private String ip;

    @ApiModelProperty(name = "关联id")
    private String relatedId;

    @ApiModelProperty(name = "最大cpu使用率")
    private float maxCpuPct;

    @ApiModelProperty(name = "最大内存使用率")
    private float maxMemoryPct;

    @ApiModelProperty(name = "在线时长")
    private float maxUptimePct;

    @ApiModelProperty(name = "宕机次数")
    private long downNum;

    @ApiModelProperty(name = "宕机时长")
    private float downTime;

    @ApiModelProperty(name = "异常次数")
    private long exeptionNum;

    @ApiModelProperty(name = "异常时长")
    private float exeptionTime;

    @ApiModelProperty(name = "进程名称")
    private String processName;
}

