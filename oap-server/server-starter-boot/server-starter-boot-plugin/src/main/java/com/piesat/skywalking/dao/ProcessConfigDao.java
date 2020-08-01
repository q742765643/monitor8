package com.piesat.skywalking.dao;

import com.piesat.common.jpa.BaseDao;
import com.piesat.skywalking.entity.ProcessConfigEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessConfigDao extends BaseDao<ProcessConfigEntity> {
}
