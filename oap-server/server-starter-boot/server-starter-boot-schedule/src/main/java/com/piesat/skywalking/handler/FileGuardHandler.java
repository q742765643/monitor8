package com.piesat.skywalking.handler;

import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.enums.FileEnum;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.folder.base.FileBaseService;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : FileGuardHandler
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-23 19:30
 */
@Slf4j
@Service("fileMonitorHandler")
public class FileGuardHandler implements BaseHandler {
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        FileMonitorDto monitor = (FileMonitorDto) jobContext.getHtJobInfoDto();
        List<Map<String, Object>> fileList = new ArrayList<>();
        FileBaseService fileBaseService = FileEnum.match(monitor.getScanType()).getBean();
        fileBaseService.singleFile(monitor, fileList, resultT);
    }

}

