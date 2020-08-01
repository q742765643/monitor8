package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileMonitorDto extends HtJobInfoDto {
    @ApiModelProperty(value = "资料文件目录规则")
    private String folderRegular;
    @ApiModelProperty(value = "资料文件名规则 正则")
    private String filenameRegular;
    @ApiModelProperty(value = "文件路径样例")
    private String fileSample;
    @ApiModelProperty(value = "应到数量")
    private long fileNum;
    @ApiModelProperty(value = "应到大小")
    private long fileSize;
}
