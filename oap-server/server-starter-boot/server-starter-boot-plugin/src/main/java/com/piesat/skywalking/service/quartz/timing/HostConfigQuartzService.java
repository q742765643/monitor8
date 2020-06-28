package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.model.QuartzModel;
import com.piesat.skywalking.service.host.HostConfigService;
import com.piesat.skywalking.service.quartz.QuartzService;
import com.piesat.skywalking.service.quartz.bean.AutoDiscoveryJob;
import com.piesat.skywalking.service.quartz.bean.HostConfigJob;
import lombok.SneakyThrows;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HostConfigQuartzService  extends QuartzService {
    @Autowired
    private HostConfigService hostConfigService;
    @SneakyThrows
    @Override
    public void addJobByType(Object o)  {
        HostConfigEntity hostConfig = (HostConfigEntity) o;
        QuartzModel quartzModel=new QuartzModel();
        quartzModel.setJobGroup("-1");
        quartzModel.setJobName(hostConfig.getId());
        quartzModel.setJobClass(HostConfigJob.class);
        Map<String,Object> map=new HashMap<>();
        map.put("ip",hostConfig.getIp());
        quartzModel.setJobDataMap(map);
        quartzModel.setCronExpression(hostConfig.getCron());
        this.addJob(quartzModel);
    }

    @SneakyThrows
    @Override
    public void initJob() {
        HostConfigEntity hostConfig=new HostConfigEntity();
        hostConfig.setStatus("1");
        hostConfig.setIsSnmp("1");
        List<HostConfigEntity> hostConfigEntities=hostConfigService.selectBySpecification(hostConfig);
        if(null!=hostConfigEntities&&!hostConfigEntities.isEmpty()){
            for(HostConfigEntity o:hostConfigEntities){
                this.addJobByType(o);
            }
        }
    }
}
