package com.piesat.skywalking.service.discovery;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.dao.AutoDiscoveryDao;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.mapstruct.AutoDiscoveryMapstruct;
import com.piesat.skywalking.service.quartz.timing.AutoDiscoveryQuartzService;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import jdk.internal.dynalink.support.AutoDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoDiscoveryServiceImpl extends BaseService<AutoDiscoveryEntity> implements AutoDiscoveryService {
    @Autowired
    private AutoDiscoveryDao autoDiscoveryDao;
    @Autowired
    private AutoDiscoveryQuartzService autoDiscoveryQuartzService;
    @Autowired
    private AutoDiscoveryMapstruct autoDiscoveryMapstruct;
    @Override
    public BaseDao<AutoDiscoveryEntity> getBaseDao() {
        return autoDiscoveryDao;
    }
    public PageBean selectPageList(PageForm<AutoDiscoveryDto> pageForm) {
        AutoDiscoveryEntity discovery=autoDiscoveryMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(discovery.getIpRange())) {
            specificationBuilder.addOr("ipRange", SpecificationOperator.Operator.likeAll.name(), discovery.getIpRange());
        }
        if (StringUtils.isNotNullString(discovery.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), discovery.getTaskName());
        }
        if (StringUtils.isNotNullString((String) discovery.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) discovery.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) discovery.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) discovery.getParamt().get("endTime"));
        }
        if (null!=discovery.getTriggerStatus()){
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), discovery.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }

    public List<AutoDiscoveryDto> selectBySpecification(AutoDiscoveryDto autoDiscoveryDto){
        AutoDiscoveryEntity autoDiscoveryEntity=autoDiscoveryMapstruct.toEntity(autoDiscoveryDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(autoDiscoveryEntity.getIpRange())) {
            specificationBuilder.addOr("ipRange", SpecificationOperator.Operator.likeAll.name(), autoDiscoveryEntity.getIpRange());
        }
        if (StringUtils.isNotNullString(autoDiscoveryEntity.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), autoDiscoveryEntity.getTaskName());
        }
        if (StringUtils.isNotNullString((String) autoDiscoveryEntity.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) autoDiscoveryEntity.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) autoDiscoveryEntity.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) autoDiscoveryEntity.getParamt().get("endTime"));
        }
        if (null!=autoDiscoveryEntity.getTriggerStatus()){
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), autoDiscoveryEntity.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<AutoDiscoveryEntity> discoveryEntities=this.getAll(specification);
        return autoDiscoveryMapstruct.toDto(discoveryEntities);
    }

    @Override
    @Transactional
    public AutoDiscoveryDto save(AutoDiscoveryDto autoDiscoveryDto){
        if(autoDiscoveryDto.getTriggerType()==null){
            autoDiscoveryDto.setTriggerType(1);
        }
        if(autoDiscoveryDto.getTriggerStatus()==null){
            autoDiscoveryDto.setTriggerStatus(0);
        }
        autoDiscoveryDto.setIsUt(0);
        autoDiscoveryDto.setDelayTime(0);
        autoDiscoveryDto.setJobHandler("autoDiscoveryHandler");
        AutoDiscoveryEntity autoDiscoveryEntity=autoDiscoveryMapstruct.toEntity(autoDiscoveryDto);
        autoDiscoveryEntity=super.saveNotNull(autoDiscoveryEntity);
        autoDiscoveryQuartzService.handleJob(autoDiscoveryMapstruct.toDto(autoDiscoveryEntity));
        return autoDiscoveryDto;
    }

    @Override
    public AutoDiscoveryDto findById(String discoveryId) {
        return autoDiscoveryMapstruct.toDto(super.getById(discoveryId));
    }

    @Override
    public void deleteByIds(List<String> ids) {
       super.deleteByIds(ids);
       autoDiscoveryQuartzService.deleteJob(ids);
    }
}
