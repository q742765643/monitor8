package com.piesat.skywalking.service.quartz.timing;

import com.piesat.enums.MonitorTypeEnum;
import com.piesat.skywalking.api.alarm.AlarmConfigService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.service.timing.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmConfigQuartzService extends ScheduleService {
    @Autowired
    private AlarmConfigService alarmConfigService;

    public void initJob() {
        AlarmConfigDto alarmConfigDto = new AlarmConfigDto();
        List<AlarmConfigDto> alarmConfigDtos = alarmConfigService.selectBySpecification(alarmConfigDto);
        if (null != alarmConfigDto && !alarmConfigDtos.isEmpty()) {
            for (AlarmConfigDto o : alarmConfigDtos) {
                this.handleJob(o);
            }
        }
    }
}
