package com.piesat.skywalking.mapper;

import com.piesat.skywalking.entity.EdgesEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface EdgesMapper {
    List<String> selectBySource(@Param("source") String source);

    int deleteBySource(@Param("source") String source);

    List<EdgesEntity> selectAllWithHost();

}
