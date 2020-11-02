package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.service.timing.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoDiscoveryQuartzService extends ScheduleService {
    @Autowired
    private AutoDiscoveryService autoDiscoveryService;


    public void initJob() {
        AutoDiscoveryDto autoDiscoveryDto = new AutoDiscoveryDto();
        List<AutoDiscoveryDto> autoDiscoveryEntities = autoDiscoveryService.selectBySpecification(autoDiscoveryDto);
        if (null != autoDiscoveryEntities && !autoDiscoveryEntities.isEmpty()) {
            for (AutoDiscoveryDto o : autoDiscoveryEntities) {
                this.handleJob(o);
            }
        }
    }
}
