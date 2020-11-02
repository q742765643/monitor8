package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.service.timing.JobInfoService;
import com.piesat.skywalking.service.timing.ScheduleService;
import com.piesat.util.NullUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName : JobInfoQuartzService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-15 18:10
 */
@Service
public class JobInfoQuartzService extends ScheduleService {
    @Autowired
    private JobInfoService jobInfoService;

    public void initJob() {
        HtJobInfoDto htJobInfoDto = new HtJobInfoDto();
        NullUtil.changeToNull(htJobInfoDto);
        List<HtJobInfoDto> htJobInfoDtos = jobInfoService.selectBySpecification(htJobInfoDto);
        if (null != htJobInfoDtos && !htJobInfoDtos.isEmpty()) {
            for (HtJobInfoDto o : htJobInfoDtos) {
                this.handleJob(o);
            }
        }
    }
}

