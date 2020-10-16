package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.mapstruct.HostConfigMapstruct;
import com.piesat.skywalking.service.host.HostConfigServiceImpl;

import com.piesat.skywalking.service.timing.ScheduleService;

import com.piesat.util.NullUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HostConfigQuartzService  extends ScheduleService {
    @Autowired
    private HostConfigService hostConfigService;


    public void initJob() {
        HostConfigDto hostConfig=new HostConfigDto();
        NullUtil.changeToNull(hostConfig);
        List<HostConfigDto> hostConfigEntities=hostConfigService.selectBySpecification(hostConfig);
        if(null!=hostConfigEntities&&!hostConfigEntities.isEmpty()){
            for(HostConfigDto o:hostConfigEntities){
                this.handleJob(o);
            }
        }
    }
}
