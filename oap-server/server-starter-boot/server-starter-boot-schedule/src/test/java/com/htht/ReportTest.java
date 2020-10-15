package com.htht;

import com.piesat.skywalking.ScheduleApplication;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.schedule.service.report.DeviceReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : ReportTest
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-15 10:42
 */
@RunWith(SpringRunner.class)
//启动Spring
@SpringBootTest(classes = ScheduleApplication.class )
public class ReportTest {
    @Autowired
    private DeviceReportService deviceReportService;

    @Test
    public void testCpu() throws Exception {
        SystemQueryDto systemQueryDto=new SystemQueryDto();
        //systemQueryDto.setIp("10.1.100.69");
        systemQueryDto.setStartTime("2020-10-14 00:00:00");
        systemQueryDto.setEndTime("2020-10-16 00:00:00");
        Map<String, Map<String,Object>> baseInfo=new HashMap<>();
        deviceReportService.getSystem(systemQueryDto);
    }
}

