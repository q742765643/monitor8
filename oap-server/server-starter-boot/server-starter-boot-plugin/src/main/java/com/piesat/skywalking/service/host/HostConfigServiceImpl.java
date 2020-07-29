package com.piesat.skywalking.service.host;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dao.HostConfigDao;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.entity.AutoDiscoveryEntity;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.mapstruct.HostConfigMapstruct;
import com.piesat.skywalking.service.quartz.timing.HostConfigQuartzService;
import com.piesat.skywalking.vo.NetworkVo;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public PageBean selectPageList(PageForm<HostConfigDto> pageForm) {
        HostConfigEntity host=hostConfigMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(host.getIp())) {
            specificationBuilder.addOr("ip", SpecificationOperator.Operator.likeAll.name(), host.getIp());
        }
        if (StringUtils.isNotNullString(host.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), host.getTaskName());
        }
        if (StringUtils.isNotNullString((String) host.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) host.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) host.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) host.getParamt().get("endTime"));
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

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
        hostConfigDto.setIsUt(0);
        hostConfigDto.setDelayTime(0);
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

    @Override
    public HostConfigDto findById(String id) {
        return hostConfigMapstruct.toDto(super.getById(id));
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
    }

    public List<String> selectNosnmp(){
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        specificationBuilder.add("isSnmp", SpecificationOperator.Operator.eq.name(), "1");
        specificationBuilder.addOr("isAgent", SpecificationOperator.Operator.eq.name(), "1");
        Specification specification = specificationBuilder.generateSpecification();
        List<HostConfigEntity> hostConfigEntities=this.getAll(specification);
        List<String> ips=new ArrayList<>();
        for(int i=0;i<hostConfigEntities.size();i++){
            ips.add(hostConfigEntities.get(i).getIp());
        }
        return ips;

    }

}
