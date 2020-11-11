package com.piesat.skywalking.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface HostConfigMapper {
    List<Map<String, Object>> findStateStatistics();

}
