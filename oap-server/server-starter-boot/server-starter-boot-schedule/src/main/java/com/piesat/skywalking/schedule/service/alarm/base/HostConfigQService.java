package com.piesat.skywalking.schedule.service.alarm.base;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName : HostConfigService
 * @Description :
 * @Author : zzj
 * @Date: 2020-11-03 14:10
 */
@Service
public class HostConfigQService {
    @GrpcHthtClient
    private HostConfigService hostConfigService;

    public List<HostConfigDto> selectAvailable() {
        return hostConfigService.selectAvailable();
    }
}

