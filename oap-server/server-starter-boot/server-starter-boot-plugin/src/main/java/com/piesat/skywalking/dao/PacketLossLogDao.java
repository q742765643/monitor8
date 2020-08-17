package com.piesat.skywalking.dao;

import com.piesat.common.jpa.BaseDao;
import com.piesat.skywalking.entity.PacketLossLogEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PacketLossLogDao extends BaseDao<PacketLossLogEntity> {
}
