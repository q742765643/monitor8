package com.piesat.skywalking.service.common;

import com.piesat.skywalking.api.common.OverdueCleanService;
import com.piesat.skywalking.mapper.OverdueCleanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName : OverdueCleanServiceImpl
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-03 18:00
 */
@Service
public class OverdueCleanServiceImpl implements OverdueCleanService {
    @Autowired
    private OverdueCleanMapper overdueCleanMapper;

    public int deleteRecord(String table,String endTime){
         overdueCleanMapper.deleteRecord(table,endTime);
         overdueCleanMapper.optimizeTable(table);
        return 1;
    }
}

