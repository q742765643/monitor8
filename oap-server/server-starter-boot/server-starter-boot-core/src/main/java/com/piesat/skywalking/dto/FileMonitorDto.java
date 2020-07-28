package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import lombok.Data;

@Data
public class FileMonitorDto extends HtJobInfoDto {
    private String folderRegular;

    private String filenameRegular;

    private String fileSample;

    private long fileNum;

    private long fileSize;
}
