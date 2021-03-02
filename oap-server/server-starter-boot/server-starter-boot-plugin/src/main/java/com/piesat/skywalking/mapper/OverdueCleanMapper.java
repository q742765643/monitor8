package com.piesat.skywalking.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
@Component
public interface OverdueCleanMapper {

    public int deleteRecord(@Param("table") String table, @Param("endTime") String endTime);

    public void optimizeTable(@Param("table") String table);

}
