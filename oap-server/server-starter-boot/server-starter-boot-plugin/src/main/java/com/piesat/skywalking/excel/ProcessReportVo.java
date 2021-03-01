package com.piesat.skywalking.excel;

import com.piesat.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : ProcessReportDto
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-03 10:01
 */
@Data
public class ProcessReportVo {



    @Excel(name = "ip",height = 55)
    @ApiModelProperty(name = "ip")
    private String ip;

    @Excel(name = "进程名称",height = 55)
    @ApiModelProperty(name = "进程名称")
    private String processName;

    @Excel(name = "告警次数",height = 55)
    @ApiModelProperty(name = "告警次数")
    private long alarmNum;

    @Excel(name = "平均cpu使用率",height = 55)
    @ApiModelProperty(name = "平均cpu使用率")
    private float avgCpuPct;

    @ApiModelProperty(name = "平均内存使用率")
    private float avgMemoryPct;



    @ApiModelProperty(name = "关联id")
    private String relatedId;

    @Excel(name = "最大cpu使用率",height = 55)
    @ApiModelProperty(name = "最大cpu使用率")
    private float maxCpuPct;

    //@Excel(name = "最大内存使用率",height = 55)
    @ApiModelProperty(name = "最大内存使用率")
    private float maxMemoryPct;

    @Excel(name = "在线时长",height = 55)
    @ApiModelProperty(name = "在线时长")
    private float maxUptimePct;

    @Excel(name = "离线次数",height = 55)
    @ApiModelProperty(name = "宕机次数")
    private long downNum;

    @Excel(name = "离线时长",height = 55)
    @ApiModelProperty(name = "宕机时长")
    private float downTime;

    @Excel(name = "异常次数",height = 55)
    @ApiModelProperty(name = "异常次数")
    private long exeptionNum;

    @Excel(name = "异常时长",height = 55)
    @ApiModelProperty(name = "异常时长")
    private float exeptionTime;

    @Excel(name = "平均内存(K)",height = 55)
    private float avgMemorySize;
    @Excel(name = "最大内存(K)",height = 55)
    private float maxMemorySize;

}

