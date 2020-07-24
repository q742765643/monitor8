package com.piesat.skywalking.service.host;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dao.HostConfigDao;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.mapstruct.HostConfigMapstruct;
import com.piesat.skywalking.service.quartz.timing.HostConfigQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HostConfigServiceImpl extends BaseService<HostConfigEntity> implements HostConfigService  {
    @Autowired
    private HostConfigDao hostConfigDao;
    @Autowired
    private HostConfigQuartzService hostConfigQuartzService;
    @Autowired
    private HostConfigMapstruct hostConfigMapstruct;
    @Override
    public BaseDao<HostConfigEntity> getBaseDao() {
        return hostConfigDao;
    }


    public List<HostConfigDto> selectBySpecification(HostConfigDto hostConfigdto){
        HostConfigEntity hostConfig=hostConfigMapstruct.toEntity(hostConfigdto);
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
        if (null!=hostConfig.getTriggerStatus()){
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), hostConfig.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<HostConfigEntity> hostConfigEntities=this.getAll(specification);
        return hostConfigMapstruct.toDto(hostConfigEntities);
    }
    @Transactional
    public HostConfigDto save(HostConfigDto hostConfigDto){
        if(hostConfigDto.getTriggerType()==null){
            hostConfigDto.setTriggerType(0);
        }
        if(hostConfigDto.getTriggerStatus()==null){
            hostConfigDto.setTriggerStatus(1);
        }
        hostConfigDto.setJobHandler("hostConfigHandler");
        HostConfigEntity hostConfig = hostConfigMapstruct.toEntity(hostConfigDto);
        hostConfig=super.saveNotNull(hostConfig);
        hostConfigQuartzService.handleJob(hostConfigMapstruct.toDto(hostConfig));
        return hostConfigMapstruct.toDto(hostConfig);
    }
    public List<HostConfigDto> selectAll(){
        Sort sort = Sort.by("ip");
        List<HostConfigEntity> list = super.getAll(sort);
        return hostConfigMapstruct.toDto(list);
    }
}
