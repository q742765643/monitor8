package com.piesat.skywalking.dto.model;

import com.piesat.util.BaseDto;
import lombok.Data;

@Data
public class HtJobInfoDto extends BaseDto {
    private long triggerLastTime;	// 上次调度时间
    private long triggerNextTime;
    private Integer triggerStatus;
    private String jobCron;
    private String taskName;
    private String jobDesc;
    private String JobHandler;
    private Integer triggerType; //0 普通 1分片
    private Integer isUt;
    private long delayTime;
    private long triggerTime;
    private Integer isAlarm;
    private String address;
}
