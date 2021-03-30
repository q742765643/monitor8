package com.piesat.skywalking.dao;

import com.piesat.common.jpa.BaseDao;
import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.entity.NetDiscoveryEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface NetDiscoveryDao extends BaseDao<NetDiscoveryEntity> {
}
