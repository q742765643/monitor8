package com.piesat.ucenter.rpc.mapstruct.dictionary;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.ucenter.entity.dictionary.LevelEntity;
import com.piesat.ucenter.rpc.dto.dictionary.LevelDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * @author yaya
 * @description
 * @date 2019/12/23
 */
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LevelMapstruct extends BaseMapper<LevelDto, LevelEntity> {
}
