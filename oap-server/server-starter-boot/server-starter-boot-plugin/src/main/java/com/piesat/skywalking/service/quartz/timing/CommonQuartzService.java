/*
package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.service.timing.ScheduleService;
import org.springframework.stereotype.Service;

@Service
public class CommonQuartzService extends ScheduleService {
    public void initJob() {
        HtJobInfoDto htJobInfoDto=new HtJobInfoDto();
        htJobInfoDto.setId("1");
        htJobInfoDto.setTriggerType(1);
        //htJobInfoDto.setJobCron("0 0 12 * * ?");
        htJobInfoDto.setJobCron("0 0/2 * * * ?");
        htJobInfoDto.setJobHandler("fileStatisticsHandler");
        htJobInfoDto.setTriggerStatus(1);
        htJobInfoDto.setTaskName("定时生成文件任务监控");
        this.handleJob(htJobInfoDto);
    }
}
*/
