package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FileStatisticsDto {
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "任务Id")
    private String taskId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "扫描规则")
    private String filenameRegular;

    @ApiModelProperty(value = "应到文件数量")
    private long fileNum;

    @ApiModelProperty(value = "应到文件大小")
    private long fileSize;

    @ApiModelProperty(value = "晚到数")
    private long lateNum;

    @ApiModelProperty(value = "实到文件数量")
    private long realFileNum;

    @ApiModelProperty(value = "实到文件大小")
    private long realFileSize;

    @ApiModelProperty(value = "文件数量到达率")
    private float perFileNum;

    @ApiModelProperty(value = "文件大小到达率")
    private float perFileSize;

    @ApiModelProperty(value = "及时率")
    private float timelinessRate;

    @ApiModelProperty(value = "应开始时间long型")
    private long startTimeL;

    @ApiModelProperty(value = "应开始时间")
    private Date startTimeS;

    @ApiModelProperty(value = "实际开始实际")
    private Date startTimeA;

    @ApiModelProperty(value = "结束时间")
    private Date endTimeA;


    @ApiModelProperty(value = "当前状态 0 一般 1 危险 2故障 3正常 4 未执行")
    private Integer status;
    @ApiModelProperty(value = "插入时间")
    private Date timestamp;
    @ApiModelProperty(value = "级别")
    private long level;

    @ApiModelProperty(value = "资料时间")
    private Date ddataTime;

    private String folderRegular;

    private String errorReason;
    private String remark;
    private String ip;

}
