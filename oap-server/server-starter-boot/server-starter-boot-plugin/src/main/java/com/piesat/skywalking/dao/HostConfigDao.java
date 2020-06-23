package com.piesat.skywalking.dao;

import com.piesat.common.jpa.BaseDao;
import com.piesat.skywalking.entity.HostConfigEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface HostConfigDao extends BaseDao<HostConfigEntity> {
}
