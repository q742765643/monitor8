package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class AlarmLogDto extends BaseDto {
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "主机id")
    private String hostId;

    @ApiModelProperty(value = "设备类型 11 未知 0 服务器 1网络设备 2进程 3文件")
    private Integer deviceType =-1;

    @ApiModelProperty(value = "详细设备类型 11 未知 0 windows 1 linux 2 二层交换机 3 三层交换机 4 路由")
    private Integer mediaType =-1;

    @ApiModelProperty(value = "告警级别 0 一般 1危险 2故障 3 正常")
    private Integer level =-1;

    @ApiModelProperty(value = "处理状态 0 未处理 1已处理")
    private Integer status =-1;

    @ApiModelProperty(value = "告警时间")
    private Date timestamp;

    @ApiModelProperty(value = "告警信息")
    private String message;

    @ApiModelProperty(value = "告警实际值")
    private float usage;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "告警监测类型")
    private Integer monitorType=-1;

    private boolean isAlarm=false;

    @ApiModelProperty(value = "process_id")
    private String processId;
}
