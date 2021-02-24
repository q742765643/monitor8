package com.piesat.skywalking.mapstruct;

import com.piesat.common.jpa.BaseMapper;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.EdgesDto;
import com.piesat.skywalking.entity.AlarmConfigEntity;
import com.piesat.skywalking.entity.EdgesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * @ClassName : EdgesMapstruct
 * @Description :
 * @Author : zzj
 * @Date: 2021-02-23 10:28
 */
@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EdgesMapstruct extends BaseMapper<EdgesDto, EdgesEntity> {
}

