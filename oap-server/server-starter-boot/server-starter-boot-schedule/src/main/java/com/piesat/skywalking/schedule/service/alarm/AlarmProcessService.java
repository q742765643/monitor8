package com.piesat.skywalking.schedule.service.alarm;

import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.schedule.service.alarm.base.AlarmBaseService;
import com.piesat.util.ResultT;
import org.springframework.stereotype.Service;

/**
 * @ClassName : AlarmProcessService
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-02 11:09
 */
@Service
public class AlarmProcessService extends AlarmBaseService {
    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {

    }
}

