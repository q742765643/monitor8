package com.piesat.skywalking.excel;

import com.piesat.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName : HostExcelVo
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-10 14:24
 */
@Data
public class HostExcelVo {
    @Excel(name = "设备别名",height = 55)
    private String hostName;

    @Excel(name = "ip地址",height = 55)
    private String ip;

    @Excel(name = "最新时间", dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT,height = 55)
    private Date updateTime;

    @Excel(name = "总在线时长(小时)",height = 55)
    private long maxUptime;

    @Excel(name = "告警次数",height = 55)
    private long alarmCount;

    @Excel(name = "宕机次数",height = 55)
    private long downCount;

    @Excel(name = "宕机时长",height = 55)
    private float downTime;

    @Excel(name = "最大cpu使用率",height = 55)
    private float maxCpuPct;

    @Excel(name = "最大内存使用率",height = 55)
    private float maxMemoryPct;

    @Excel(name = "最大文件使用率",height = 55)
    private float maxFilesystemPct;

    @Excel(name = "最大进程数",height = 55)
    private long maxProcessSize;


}

