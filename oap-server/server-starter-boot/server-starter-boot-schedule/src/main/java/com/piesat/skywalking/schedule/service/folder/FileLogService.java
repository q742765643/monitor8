package com.piesat.skywalking.schedule.service.folder;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.folder.FileMonitorLogService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import org.springframework.stereotype.Service;

/**
 * @ClassName : FileLogService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-26 10:39
 */
@Service
public class FileLogService {
    @GrpcHthtClient
    private FileMonitorLogService fileMonitorLogService;

    public FileMonitorLogDto insertLog(FileMonitorDto fileMonitorDto) {
        FileMonitorLogDto fileMonitorLogDto = new FileMonitorLogDto();
        fileMonitorLogDto.setHandleCode(0);
        fileMonitorLogDto.setTaskId(fileMonitorDto.getId());
        fileMonitorLogDto.setTriggerTime(fileMonitorDto.getTriggerLastTime());
        fileMonitorLogDto.setFilenameRegular(fileMonitorDto.getFilenameRegular());
        fileMonitorLogDto.setFolderRegular(fileMonitorDto.getFolderRegular());
        fileMonitorLogDto.setFileNum(fileMonitorDto.getFileNum());
        fileMonitorLogDto.setFileSize(fileMonitorDto.getFileSize());
        fileMonitorLogDto.setIsUt(fileMonitorDto.getIsUt());
        fileMonitorLogDto.setJobCron(fileMonitorDto.getJobCron());
        fileMonitorLogDto.setTaskName(fileMonitorDto.getTaskName());
        fileMonitorLogDto.setIsCompensation(fileMonitorDto.getIsCompensation());
        return fileMonitorLogService.save(fileMonitorLogDto);
    }

    public void updateLog(FileMonitorLogDto fileMonitorLogDto) {
        fileMonitorLogService.save(fileMonitorLogDto);
    }
}

