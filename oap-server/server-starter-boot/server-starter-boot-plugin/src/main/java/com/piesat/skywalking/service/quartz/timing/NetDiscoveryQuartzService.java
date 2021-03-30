package com.piesat.skywalking.service.quartz.timing;

import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.api.discover.NetDiscoveryService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.skywalking.service.timing.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetDiscoveryQuartzService extends ScheduleService {
    @Autowired
    private NetDiscoveryService netDiscoveryService;


    public void initJob() {
        NetDiscoveryDto netDiscoveryDto = new NetDiscoveryDto();
        List<NetDiscoveryDto> netDiscoveryDtos = netDiscoveryService.selectBySpecification(netDiscoveryDto);
        if (null != netDiscoveryDtos && !netDiscoveryDtos.isEmpty()) {
            for (NetDiscoveryDto o : netDiscoveryDtos) {
                this.handleJob(o);
            }
        }
    }
}
