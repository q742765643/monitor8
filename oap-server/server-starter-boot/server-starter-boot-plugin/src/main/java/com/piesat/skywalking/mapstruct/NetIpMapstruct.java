package com.piesat.skywalking.mapstruct;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.skywalking.dto.NetIpDto;
import com.piesat.skywalking.entity.NetDiscoveryEntity;
import com.piesat.skywalking.entity.NetIpEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NetIpMapstruct extends BaseMapper<NetIpDto, NetIpEntity> {
}
