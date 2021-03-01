package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName : FileMonitorLogDto
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-26 09:40
 */
@Data
public class FileMonitorLogDto extends BaseDto {
    @ApiModelProperty(value = "任务ID")
    private String taskId;
    @ApiModelProperty(value = "远程目录")
    private String remotePath;
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
    @ApiModelProperty(value = "是否补偿 0否 1是")
    private Integer isCompensation = 0;
    @ApiModelProperty(value = "提取时间")
    private String expression;
    @ApiModelProperty(value = "是否世界时")
    private Integer isUt;
    @ApiModelProperty(value = "cron表达式")
    private String jobCron;
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    private Integer status;
    @ApiModelProperty(value = "提取全正则")
    private String allExpression;
    @ApiModelProperty(value = "资料时间")
    private Date ddataTime;
    private String errorReason;
    private String remark;
}

