package com.piesat.skywalking.handler;

import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.schedule.service.report.OverviewService;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

/**
 * @ClassName : OverviewHandler
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-19 14:49
 */
@Slf4j
@Service("overviewHandler")
public class OverviewHandler implements BaseHandler {
    @Autowired
    private OverviewService overviewService;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
        String endTime = dateFormat.format(time);
        String starTime = dateFormat.format(time - 600000);
        SystemQueryDto systemQueryDto = new SystemQueryDto();
        systemQueryDto.setStartTime(starTime);
        systemQueryDto.setEndTime(endTime);
        overviewService.getSystem(systemQueryDto);
    }
}

