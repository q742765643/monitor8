package com.piesat.skywalking.service.discovery;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.discover.AutoDiscoveryService;
import com.piesat.skywalking.api.discover.NetDiscoveryService;
import com.piesat.skywalking.api.discover.NetIpService;
import com.piesat.skywalking.dao.AutoDiscoveryDao;
import com.piesat.skywalking.dao.NetDiscoveryDao;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.skywalking.entity.NetDiscoveryEntity;
import com.piesat.skywalking.mapstruct.NetDiscoveryMapstruct;
import com.piesat.skywalking.service.quartz.timing.AutoDiscoveryQuartzService;
import com.piesat.skywalking.service.quartz.timing.NetDiscoveryQuartzService;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NetDiscoveryServiceImpl extends BaseService<NetDiscoveryEntity> implements NetDiscoveryService {
    @Autowired
    private NetDiscoveryDao netDiscoveryDao;
    @Autowired
    private NetDiscoveryQuartzService netDiscoveryQuartzService;
    @Autowired
    private NetDiscoveryMapstruct netDiscoveryMapstruct;
    @Autowired
    private NetIpService netIpService;

    @Override
    public BaseDao<NetDiscoveryEntity> getBaseDao() {
        return netDiscoveryDao;
    }

    public PageBean selectPageList(PageForm<NetDiscoveryDto> pageForm) {
        NetDiscoveryEntity discovery = netDiscoveryMapstruct.toEntity(pageForm.getT());
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
        if (null != discovery.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), discovery.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }

    public List<NetDiscoveryDto> selectBySpecification(NetDiscoveryDto netDiscoveryDto) {
        NetDiscoveryEntity netDiscoveryEntity = netDiscoveryMapstruct.toEntity(netDiscoveryDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(netDiscoveryEntity.getIpRange())) {
            specificationBuilder.addOr("ipRange", SpecificationOperator.Operator.likeAll.name(), netDiscoveryEntity.getIpRange());
        }
        if (StringUtils.isNotNullString(netDiscoveryEntity.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), netDiscoveryEntity.getTaskName());
        }
        if (StringUtils.isNotNullString((String) netDiscoveryEntity.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) netDiscoveryEntity.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) netDiscoveryEntity.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) netDiscoveryEntity.getParamt().get("endTime"));
        }
        if (null != netDiscoveryDto.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), netDiscoveryDto.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<NetDiscoveryEntity> discoveryEntities = this.getAll(specification);
        return netDiscoveryMapstruct.toDto(discoveryEntities);
    }

    @Override
    @Transactional
    public NetDiscoveryDto save(NetDiscoveryDto netDiscoveryDto) {
        if (netDiscoveryDto.getTriggerStatus() == null) {
            netDiscoveryDto.setTriggerStatus(0);
        }
        netDiscoveryDto.setIsUt(0);
        netDiscoveryDto.setDelayTime(0);
        netDiscoveryDto.setJobHandler("netDiscoveryHandler");
        NetDiscoveryEntity netDiscoveryEntity = netDiscoveryMapstruct.toEntity(netDiscoveryDto);
        netDiscoveryEntity = super.saveNotNull(netDiscoveryEntity);
        netDiscoveryQuartzService.handleJob(netDiscoveryMapstruct.toDto(netDiscoveryEntity));
        return netDiscoveryDto;
    }

    public NetDiscoveryDto updateAutoDiscovery(NetDiscoveryDto netDiscoveryDto) {
        NetDiscoveryEntity netDiscoveryEntity = netDiscoveryMapstruct.toEntity(netDiscoveryDto);
        netDiscoveryEntity = super.saveNotNull(netDiscoveryEntity);
        netDiscoveryQuartzService.handleJob(netDiscoveryMapstruct.toDto(netDiscoveryEntity));
        return netDiscoveryDto;
    }

    @Override
    public NetDiscoveryDto findById(String discoveryId) {
        return netDiscoveryMapstruct.toDto(super.getById(discoveryId));
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
        netDiscoveryQuartzService.deleteJob(ids);
        try {
            for(String id:ids){
                netIpService.deleteByWhere(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trigger(String id){
        netDiscoveryQuartzService.trigger(this.findById(id));
    }

}
