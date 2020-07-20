package com.piesat.ucenter.rpc.mapstruct.dictionary;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.dictionary.DefineEntity;
import com.piesat.ucenter.rpc.dto.dictionary.DefineDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * @author yaya
 * @description TODO
 * @date 2019/12/26 17:20
 */
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DefineMapstruct extends BaseMapper<DefineDto, DefineEntity> {
}
