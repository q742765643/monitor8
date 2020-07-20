package com.piesat.ucenter.rpc.mapstruct.monitor;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.monitor.LoginInfoEntity;
import com.piesat.ucenter.rpc.dto.monitor.LoginInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-17 14:15
 **/
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginInfoMapstruct extends BaseMapper<LoginInfoDto,LoginInfoEntity>{
}

