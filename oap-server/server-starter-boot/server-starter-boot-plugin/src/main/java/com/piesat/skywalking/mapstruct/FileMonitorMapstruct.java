package com.piesat.skywalking.mapstruct;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.entity.FileMonitorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMonitorMapstruct extends BaseMapper<FileMonitorDto, FileMonitorEntity> {
}
