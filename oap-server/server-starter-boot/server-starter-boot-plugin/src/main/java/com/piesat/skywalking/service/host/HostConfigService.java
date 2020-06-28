package com.piesat.skywalking.service.host;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.dao.HostConfigDao;
import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.service.quartz.timing.HostConfigQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HostConfigService extends BaseService<HostConfigEntity> {
    @Autowired
    private HostConfigDao hostConfigDao;
    @Autowired
    private HostConfigQuartzService hostConfigQuartzService;
    @Override
    public BaseDao<HostConfigEntity> getBaseDao() {
        return hostConfigDao;
    }


    public List<HostConfigEntity> selectBySpecification(HostConfigEntity hostConfig){
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(hostConfig.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(),hostConfig.getIp());
        }
        if (StringUtils.isNotNullString((String) hostConfig.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) hostConfig.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) hostConfig.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) hostConfig.getParamt().get("endTime"));
        }
        if (StringUtils.isNotNullString(hostConfig.getIsSnmp())){
            specificationBuilder.add("isSnmp", SpecificationOperator.Operator.eq.name(), hostConfig.getIsSnmp());
        }
        if (StringUtils.isNotNullString(hostConfig.getStatus())){
            specificationBuilder.add("status", SpecificationOperator.Operator.eq.name(), hostConfig.getStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<HostConfigEntity> hostConfigEntities=this.getAll(specification);
        return hostConfigEntities;
    }
    @Transactional
    public HostConfigEntity save(HostConfigEntity hostConfigDto){
        HostConfigEntity hostConfig = super.save(hostConfigDto);
        hostConfigQuartzService.addJobByType(hostConfig);
        return hostConfig;
    }
}
