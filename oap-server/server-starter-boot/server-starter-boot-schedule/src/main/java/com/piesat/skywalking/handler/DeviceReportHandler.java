package com.piesat.skywalking.handler;

import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.report.DeviceReportService;
import com.piesat.util.DateExpressionEngine;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName : DeviceReportHandler
 * @Description : 设备日报
 * @Author : zzj
 * @Date: 2020-10-18 14:55
 */
@Slf4j
@Service("deviceReportHandler")
public class DeviceReportHandler implements BaseHandler {
    @Autowired
    private DeviceReportService deviceReportService;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        String starTime = DateExpressionEngine.formatDateExpression("${yyyy-MM-dd 00:00:00,-0d}", time);
    }

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        long time = System.currentTimeMillis();
        String starTime = DateExpressionEngine.formatDateExpression("${yyyy-MM-dd 00:00:00,-0d}", time);
        String endTime = DateExpressionEngine.formatDateExpression("${yyyy-MM-dd 00:00:00,1d}", time);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        deviceReportService.getSystem(systemQueryDto);
    }
}
