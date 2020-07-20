package com.piesat.ucenter.rpc.mapstruct.monitor;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.monitor.OperLogEntity;
import com.piesat.ucenter.rpc.dto.monitor.OperLogDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-16 15:06
 **/
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperLogMapstruct extends BaseMapper<OperLogDto,OperLogEntity> {
}

