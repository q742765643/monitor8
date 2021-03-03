package com.piesat.skywalking.mapper;

import com.piesat.skywalking.entity.HostConfigEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface HostConfigMapper {
    List<Map<String, Object>> findStateStatistics();
    int updateStatus(HostConfigEntity hostConfigEntity);

}
