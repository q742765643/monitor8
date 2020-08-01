package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import lombok.Data;

import java.util.Date;


@Data
public class AlarmLogDto extends BaseDto {

    private String deviceName;

    private String deviceType;

    private String level;

    private String status;

    private Date timestamp;

    private String message;

    private float usage;

    private String ip;

    private String type;

    private boolean isAlarm=false;
}
