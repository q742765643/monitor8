package com.piesat.skywalking.service.discovery;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.dao.AutoDiscoveryDao;
import com.piesat.skywalking.entity.AutoDiscoveryEntity;
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
public class AutoDiscoveryService extends BaseService<AutoDiscoveryEntity> {
    @Autowired
    private AutoDiscoveryDao autoDiscoveryDao;
    @Autowired
    private AutoDiscoveryQuartzService autoDiscoveryQuartzService;
    @Override
    public BaseDao<AutoDiscoveryEntity> getBaseDao() {
        return autoDiscoveryDao;
    }
    public PageBean selectPageList(PageForm<AutoDiscoveryEntity> pageForm) {
        AutoDiscoveryEntity discovery=pageForm.getT();
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(discovery.getIpRange())) {
            specificationBuilder.addOr("ipRange", SpecificationOperator.Operator.likeAll.name(), discovery.getIpRange());
        }
        if (StringUtils.isNotNullString(discovery.getDisName())) {
            specificationBuilder.addOr("disName", SpecificationOperator.Operator.likeAll.name(), discovery.getDisName());
        }
        if (StringUtils.isNotNullString((String) discovery.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) discovery.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) discovery.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) discovery.getParamt().get("endTime"));
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }

    public List<AutoDiscoveryEntity> selectBySpecification(AutoDiscoveryEntity autoDiscoveryDto){
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(autoDiscoveryDto.getIpRange())) {
            specificationBuilder.addOr("ipRange", SpecificationOperator.Operator.likeAll.name(), autoDiscoveryDto.getIpRange());
        }
        if (StringUtils.isNotNullString(autoDiscoveryDto.getDisName())) {
            specificationBuilder.addOr("disName", SpecificationOperator.Operator.likeAll.name(), autoDiscoveryDto.getDisName());
        }
        if (StringUtils.isNotNullString((String) autoDiscoveryDto.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) autoDiscoveryDto.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) autoDiscoveryDto.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) autoDiscoveryDto.getParamt().get("endTime"));
        }
        if (StringUtils.isNotNullString(autoDiscoveryDto.getStatus())){
            specificationBuilder.add("status", SpecificationOperator.Operator.eq.name(), autoDiscoveryDto.getStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<AutoDiscoveryEntity> discoveryEntities=this.getAll(specification);
        return discoveryEntities;
    }
    @Transactional
    public AutoDiscoveryEntity save(AutoDiscoveryEntity autoDiscoveryDto){
        AutoDiscoveryEntity autoDiscoveryEntity=super.save(autoDiscoveryDto);
        autoDiscoveryQuartzService.addJobByType(autoDiscoveryEntity);
        return autoDiscoveryDto;
    }
}
