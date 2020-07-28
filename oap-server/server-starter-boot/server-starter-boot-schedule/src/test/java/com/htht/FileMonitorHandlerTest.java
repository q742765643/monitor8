package com.htht;

import com.piesat.skywalking.ScheduleApplication;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.FileMonitorHandler;
import com.piesat.util.ResultT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
//启动Spring
@SpringBootTest(classes =ScheduleApplication.class )
public class FileMonitorHandlerTest {
    @Autowired
    private FileMonitorHandler fileMonitorHandler;
    @Test
    public void test() throws Exception {
        FileMonitorDto fileMonitorDto=new FileMonitorDto();
        fileMonitorDto.setFilenameRegular("zc_mwf_height_10_${yyyyMMddHHmm}.\\d{3}");
        fileMonitorDto.setFolderRegular("F:/DQ1055-cp/T799/5.8height${yyyyMMdd}");
        fileMonitorDto.setJobCron("0 0 1 * * ?");
        fileMonitorDto.setTriggerLastTime(new Date().getTime());
        JobContext jobContext=new JobContext();
        jobContext.setHtJobInfoDto(fileMonitorDto);
        fileMonitorHandler.execute(jobContext,new ResultT<>());
    }
}
