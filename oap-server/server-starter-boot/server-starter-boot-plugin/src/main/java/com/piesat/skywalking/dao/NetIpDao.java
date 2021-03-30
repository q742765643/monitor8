package com.piesat.skywalking.dao;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.skywalking.dto.NetIpDto;
import com.piesat.skywalking.entity.NetDiscoveryEntity;
import com.piesat.skywalking.entity.NetIpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NetIpDao  extends BaseDao<NetIpEntity> {


}
