package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class AlarmLogDto extends BaseDto {
    @ApiModelProperty(value = "告警指标")
    private Integer alarmKpi;

    @ApiModelProperty(value = "相关ID")
    private String relatedId;

    @ApiModelProperty(value = "设备分类 11 未知 0 服务器 1网络设备 2进程 3文件")
    private Integer deviceType;

    @ApiModelProperty(value = "详细设备类型 11 未知 0 windows 1 linux 2 二层交换机 3 三层交换机 4 路由")
    private Integer mediaType;

    @ApiModelProperty(value = "告警级别 0 一般 1危险 2故障 3 正常")
    private Integer level;

    @ApiModelProperty(value = "处理状态 0 未处理 1已处理")
    private Integer status;

    @ApiModelProperty(value = "告警时间")
    private Date timestamp;

    @ApiModelProperty(value = "告警信息")
    private String message;

    @ApiModelProperty(value = "告警实际值")
    private float usage;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "告警监测类型")
    private Integer monitorType;

    @ApiModelProperty(value = "持续时间")
    private String duration;

    private boolean isAlarm = false;

    @ApiModelProperty(value = "一般判断条件")
    private List<ConditionDto> generals;

    @ApiModelProperty(value = "危险判断条件")
    private List<ConditionDto> dangers;

    @ApiModelProperty(value = "故障判断条件")
    private List<ConditionDto> severitys;


}
