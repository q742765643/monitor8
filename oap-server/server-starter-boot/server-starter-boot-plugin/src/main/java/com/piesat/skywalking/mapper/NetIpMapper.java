package com.piesat.skywalking.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface NetIpMapper {

    public void deleteByWhere(@Param("discoveryId") String discoveryId);

}
