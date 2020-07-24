package com.piesat.skywalking.mapstruct;


import com.piesat.common.jpa.BaseMapper;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HostConfigMapstruct extends BaseMapper<HostConfigDto, HostConfigEntity> {
}
