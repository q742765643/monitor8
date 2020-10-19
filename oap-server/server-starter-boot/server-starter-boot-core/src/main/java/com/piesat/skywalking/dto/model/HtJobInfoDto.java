package com.piesat.skywalking.dto.model;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HtJobInfoDto extends BaseDto {
    @ApiModelProperty(value = "上次调度时间")
    private long triggerLastTime;	// 上次调度时间
    @ApiModelProperty(value = "下次调度时间")
    private long triggerNextTime;
    @ApiModelProperty(value = "0 代表未启动 1代表启动")
    private Integer triggerStatus;
    @ApiModelProperty(value = "cron 表达式 必填")
    private String jobCron;
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    @ApiModelProperty(value = "任务描述")
    private String jobDesc;
    private String jobHandler;
    @ApiModelProperty(value = "触发类型 0 普通 1分片调度")
    private Integer triggerType; //0 普通 1分片
    @ApiModelProperty(value = "时区 0 代表北京时 1代表时间时")
    private Integer isUt;
    private long delayTime;
    private long triggerTime;
    private Integer isAlarm;
    @ApiModelProperty(value = "调度地址")
    private String address;
}
