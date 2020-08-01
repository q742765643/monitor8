package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class AlarmLogDto extends BaseDto {
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型 0 服务器 1网络设备 3文件")
    private String deviceType;

    @ApiModelProperty(value = "告警级别 0 一般 1危险 3故障")
    private String level;

    @ApiModelProperty(value = "处理状态 0 未处理 1已处理")
    private String status;

    @ApiModelProperty(value = "告警时间")
    private Date timestamp;

    @ApiModelProperty(value = "告警信息")
    private String message;

    @ApiModelProperty(value = "告警实际值")
    private float usage;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "告警监测类型")
    private String type;

    private boolean isAlarm=false;
}
