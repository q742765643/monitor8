package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.model.QuartzModel;
import com.piesat.skywalking.service.quartz.QuartzService;
import com.piesat.skywalking.service.quartz.bean.AutoDiscoveryJob;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AutoDiscoveryQuartzService extends QuartzService {

    @Override
    public void addJobByType(Object o) throws SchedulerException {
        AutoDiscoveryEntity autoDiscovery = (AutoDiscoveryEntity) o;
        QuartzModel quartzModel=new QuartzModel();
        quartzModel.setJobGroup("-1");
        quartzModel.setJobName(autoDiscovery.getId());
        quartzModel.setJobClass(AutoDiscoveryJob.class);
        Map<String,Object> map=new HashMap<>();
        map.put("ipRange",autoDiscovery.getIpRange());
        quartzModel.setJobDataMap(map);
        quartzModel.setCronExpression("0 */5 * * * ?");
        this.addJob(quartzModel);
    }
}
