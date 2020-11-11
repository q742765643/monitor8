package com.piesat.skywalking.handler;

import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.enums.AlarmEnum;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName : AlarmHandler
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-03 11:27
 */
@Slf4j
@Service("alarmHandler")
public class AlarmHandler implements BaseHandler {
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        AlarmConfigDto alarmConfigDto = (AlarmConfigDto) jobContext.getHtJobInfoDto();
        AlarmBaseService alarmBaseService=AlarmEnum.match(alarmConfigDto.getMonitorType()).getBean();
        alarmBaseService.execute(jobContext,resultT);
    }
}

