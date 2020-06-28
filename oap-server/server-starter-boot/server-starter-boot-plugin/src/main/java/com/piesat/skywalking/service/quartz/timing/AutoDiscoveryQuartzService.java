package com.piesat.skywalking.service.quartz.timing;

import com.piesat.common.jpa.entity.BaseEntity;
import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.model.QuartzModel;
import com.piesat.skywalking.service.discovery.AutoDiscoveryService;
import com.piesat.skywalking.service.quartz.QuartzService;
import com.piesat.skywalking.service.quartz.bean.AutoDiscoveryJob;
import lombok.SneakyThrows;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AutoDiscoveryQuartzService extends QuartzService {
    @Autowired
    private AutoDiscoveryService autoDiscoveryService;
    @SneakyThrows
    @Override
    public void addJobByType(Object o)  {
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

    @SneakyThrows
    @Override
    public void initJob() {
        AutoDiscoveryEntity autoDiscoveryDto=new AutoDiscoveryEntity();
        autoDiscoveryDto.setStatus("1");
        List<AutoDiscoveryEntity> autoDiscoveryEntities=autoDiscoveryService.selectBySpecification(autoDiscoveryDto);
        if(null!=autoDiscoveryEntities&&!autoDiscoveryEntities.isEmpty()){
            for(AutoDiscoveryEntity o:autoDiscoveryEntities){
                this.addJobByType(o);
            }
        }
    }
}
