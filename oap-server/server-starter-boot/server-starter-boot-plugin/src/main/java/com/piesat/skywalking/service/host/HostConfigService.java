package com.piesat.skywalking.service.host;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.skywalking.dao.HostConfigDao;
import com.piesat.skywalking.entity.HostConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HostConfigService extends BaseService<HostConfigEntity> {
    @Autowired
    private HostConfigDao hostConfigDao;
    @Override
    public BaseDao<HostConfigEntity> getBaseDao() {
        return hostConfigDao;
    }
}
