package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.mapstruct.HostConfigMapstruct;
import com.piesat.skywalking.service.host.HostConfigServiceImpl;

import com.piesat.skywalking.service.timing.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HostConfigQuartzService  extends ScheduleService {
    @Autowired
    private HostConfigService hostConfigService;




    public void initJob() {
        HostConfigDto hostConfig=new HostConfigDto();
        List<HostConfigDto> hostConfigEntities=hostConfigService.selectBySpecification(hostConfig);
        if(null!=hostConfigEntities&&!hostConfigEntities.isEmpty()){
            for(HostConfigDto o:hostConfigEntities){
                if(!"1".equals(o.getIsSnmp())){
                    o.setTriggerStatus(0);
                }
                if("1".equals(o.getIsAgent())){
                    o.setTriggerStatus(0);
                }
                this.handleJob(o);
            }
        }
    }
}
