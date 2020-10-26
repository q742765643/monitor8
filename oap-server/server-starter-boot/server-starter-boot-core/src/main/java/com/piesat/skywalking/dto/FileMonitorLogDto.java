package com.piesat.skywalking.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : FileMonitorLogDto
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-26 09:40
 */
@Data
public class FileMonitorLogDto {
    @ApiModelProperty(value = "任务ID")
    private String taskId;
    @ApiModelProperty(value = "文件目录")
    private String folderRegular;
    @ApiModelProperty(value = "文件名规则")
    private String filenameRegular;
    @ApiModelProperty(value = "触发时间")
    private long triggerTime;
    @ApiModelProperty(value = "执行状态 0 运行中 1成功 2 失败")
    private Integer handleCode;
    @ApiModelProperty(value = "执行过程")
    private String handleMsg;
    @ApiModelProperty(value = "执行耗时")
    private long elapsedTime;
    @ApiModelProperty(value = "应到文件数量")
    private long fileNum;
    @ApiModelProperty(value = "应到文件大小")
    private long fileSize;

    @ApiModelProperty(value = "实到文件数量")
    private long realFileNum;

    @ApiModelProperty(value = "实到文件大小")
    private long realFileSize;

    @ApiModelProperty(value = "晚到数")
    private long lateNum;


    private String expression;
}

