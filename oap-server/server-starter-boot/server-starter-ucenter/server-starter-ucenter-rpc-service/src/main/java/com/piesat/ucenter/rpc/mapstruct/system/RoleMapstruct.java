package com.piesat.ucenter.rpc.mapstruct.system;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.system.RoleEntity;
import com.piesat.ucenter.rpc.dto.system.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 16:05
 */
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapstruct extends BaseMapper<RoleDto, RoleEntity> {
}
