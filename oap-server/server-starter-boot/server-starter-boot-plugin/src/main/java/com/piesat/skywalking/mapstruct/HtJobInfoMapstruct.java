package com.piesat.skywalking.mapstruct;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.model.HtJobInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HtJobInfoMapstruct extends BaseMapper<HtJobInfoDto, HtJobInfo> {
}
