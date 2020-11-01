package com;

import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.service.report.FileQReportServiceImpl;
import com.piesat.starter.OAPServerStartUp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * @ClassName : FileTest
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-28 19:24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAPServerStartUp.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class FileTest {
    @Autowired
    private FileQReportServiceImpl fileQReportService;
    @Test
    public void testCpu() throws Exception {
        SystemQueryDto systemQueryDto=new SystemQueryDto();
        //systemQueryDto.setIp("10.1.100.69");
        systemQueryDto.setStartTime("2020-10-14 00:00:00");
        systemQueryDto.setEndTime("2020-10-20 00:00:00");
        fileQReportService.findFileReport(systemQueryDto);
    }
}

