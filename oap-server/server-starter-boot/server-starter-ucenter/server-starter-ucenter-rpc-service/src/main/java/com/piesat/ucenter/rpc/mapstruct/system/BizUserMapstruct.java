package com.piesat.ucenter.rpc.mapstruct.system;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.system.BizUserEntity;
import com.piesat.ucenter.rpc.dto.system.BizUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * 业务注册用户
 *
 * @author cwh
 * @date 2020年 04月17日 17:21:35
 */
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BizUserMapstruct extends BaseMapper<BizUserDto, BizUserEntity> {
}
