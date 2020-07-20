package com.piesat.ucenter.rpc.mapstruct.system;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.system.DeptEntity;
import com.piesat.ucenter.rpc.dto.system.DeptDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 9:48
 */
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptMapstruct extends BaseMapper<DeptDto, DeptEntity> {
}
