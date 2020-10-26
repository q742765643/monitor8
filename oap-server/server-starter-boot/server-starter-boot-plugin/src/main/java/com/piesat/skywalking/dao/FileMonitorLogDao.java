package com.piesat.skywalking.dao;

import com.piesat.common.jpa.BaseDao;
import com.piesat.skywalking.entity.FileMonitorLogEntity;
import org.springframework.stereotype.Service;

@Service
public interface FileMonitorLogDao extends BaseDao<FileMonitorLogEntity> {
}
