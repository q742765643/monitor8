package com.piesat.skywalking.excel;

import com.piesat.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName : LinkExcelVo
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-11 14:09
 */
@Data
public class LinkExcelVo {
    @Excel(name = "设备别名",height = 55)
    private String hostName;

    @Excel(name = "ip地址",height = 55)
    private String ip;

    @Excel(name = "当前状态",height = 55,readConverterExp="0=一般,1=危险,2=故障,3=正常")
    public Integer currentStatus;

    @Excel(name = "监控方式",height = 55, readConverterExp="1=代理,2=snmp,3=ping")
    private Integer monitoringMethods;

    @Excel(name = "最新时间", dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT,height = 55)
    private Date updateTime;

    @Excel(name = "总在线时长(小时)",height = 55)
    private long maxUptime;

    @Excel(name = "平均丢包率",height = 55)
    private float avgPacketPct;
    @Excel(name = "最大丢包率",height = 55)
    private float maxPacketPct;

    @Excel(name = "区域",height = 55, readConverterExp="0=办公区,1=值班区,2=其他区,3=机房区")
    private Integer area;
    @Excel(name = "地址",height = 55)
    private String location;
}

