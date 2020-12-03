package com.piesat.skywalking.service.alarm;

import com.alibaba.fastjson.JSON;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.alarm.AlarmConfigService;
import com.piesat.skywalking.dao.AlarmConfigDao;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.skywalking.dto.ConditionDto;
import com.piesat.skywalking.entity.AlarmConfigEntity;
import com.piesat.skywalking.mapstruct.AlarmConfigMapstruct;
import com.piesat.skywalking.service.quartz.timing.AlarmConfigQuartzService;
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
public class AlarmConfigServiceImpl extends BaseService<AlarmConfigEntity> implements AlarmConfigService {
    @Autowired
    private AlarmConfigDao alarmConfigDao;
    @Autowired
    private AlarmConfigMapstruct alarmConfigMapstruct;
    @Autowired
    private AlarmConfigQuartzService alarmConfigQuartzService;

    @Override
    public BaseDao<AlarmConfigEntity> getBaseDao() {
        return alarmConfigDao;
    }

    public PageBean selectPageList(PageForm<AlarmConfigDto> pageForm) {
        AlarmConfigEntity alarmConfig = alarmConfigMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (null != alarmConfig.getMonitorType() && alarmConfig.getMonitorType() > -1) {
            specificationBuilder.addOr("monitorType", SpecificationOperator.Operator.eq.name(), alarmConfig.getMonitorType());
        }
        if (StringUtils.isNotNullString(alarmConfig.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), alarmConfig.getTaskName());
        }
        if (StringUtils.isNotNullString((String) alarmConfig.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) alarmConfig.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) alarmConfig.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) alarmConfig.getParamt().get("endTime"));
        }
        if (null != alarmConfig.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), alarmConfig.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }

    public List<AlarmConfigDto> selectBySpecification(AlarmConfigDto alarmConfigDto) {
        AlarmConfigEntity alarmConfig = alarmConfigMapstruct.toEntity(alarmConfigDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (null != alarmConfig.getMonitorType()) {
            specificationBuilder.addOr("monitorType", SpecificationOperator.Operator.eq.name(), alarmConfig.getMonitorType());
        }
        if (StringUtils.isNotNullString(alarmConfig.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), alarmConfig.getTaskName());
        }
        if (StringUtils.isNotNullString((String) alarmConfig.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) alarmConfig.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) alarmConfig.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) alarmConfig.getParamt().get("endTime"));
        }
        if (null != alarmConfig.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), alarmConfig.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<AlarmConfigEntity> alarmConfigEntities = this.getAll(specification);
        List<AlarmConfigDto> alarmConfigDtos = new ArrayList<>();
        for (AlarmConfigEntity alarmConfigEntity : alarmConfigEntities) {
            AlarmConfigDto alarmConfigDto1 = alarmConfigMapstruct.toDto(alarmConfigEntity);
            alarmConfigDto1.setGenerals(JSON.parseArray(alarmConfigEntity.getGeneral(), ConditionDto.class));
            alarmConfigDto1.setDangers(JSON.parseArray(alarmConfigEntity.getDanger(), ConditionDto.class));
            alarmConfigDto1.setSeveritys(JSON.parseArray(alarmConfigEntity.getSeverity(), ConditionDto.class));
            alarmConfigDto1.setJobCron(alarmConfigEntity.getJobCron());
            alarmConfigDtos.add(alarmConfigDto1);
        }
        return alarmConfigDtos;
    }

    @Override
    @Transactional
    public AlarmConfigDto save(AlarmConfigDto alarmConfigDto) {
        if (alarmConfigDto.getTriggerType() == null) {
            alarmConfigDto.setTriggerType(0);
        }
        if (alarmConfigDto.getTriggerStatus() == null) {
            alarmConfigDto.setTriggerStatus(0);
        }
 /*       if(MonitorTypeEnum.FILE_REACH==MonitorTypeEnum.match(alarmConfigDto.getMonitorType())){
            alarmConfigDto.setTriggerStatus(0);
        }*/
        alarmConfigDto.setIsUt(0);
        alarmConfigDto.setDelayTime(0);
        alarmConfigDto.setJobHandler("alarmHandler");
        AlarmConfigEntity alarmConfigEntity = alarmConfigMapstruct.toEntity(alarmConfigDto);
        //alarmConfigEntity.setId(MonitorTypeEnum.match(alarmConfigEntity.getMonitorType()).getValue().toString());
        alarmConfigEntity.setGeneral(JSON.toJSONString(alarmConfigDto.getGenerals()));
        alarmConfigEntity.setDanger(JSON.toJSONString(alarmConfigDto.getDangers()));
        alarmConfigEntity.setSeverity(JSON.toJSONString(alarmConfigDto.getSeveritys()));
        alarmConfigEntity = super.saveNotNull(alarmConfigEntity);
        alarmConfigQuartzService.handleJob(alarmConfigMapstruct.toDto(alarmConfigEntity));
        return alarmConfigDto;
    }

    public AlarmConfigDto updateAlarm(AlarmConfigDto alarmConfigDto) {
        AlarmConfigEntity alarmConfigEntity = alarmConfigMapstruct.toEntity(alarmConfigDto);
        alarmConfigEntity = super.saveNotNull(alarmConfigEntity);
        alarmConfigQuartzService.handleJob(alarmConfigMapstruct.toDto(alarmConfigEntity));
        return alarmConfigDto;
    }

    @Override
    public AlarmConfigDto findById(String id) {
        AlarmConfigEntity alarmConfig = super.getById(id);
        if (alarmConfig == null) {
            return null;
        }
        AlarmConfigDto alarmConfigDto = alarmConfigMapstruct.toDto(alarmConfig);
        alarmConfigDto.setGenerals(JSON.parseArray(alarmConfig.getGeneral(), ConditionDto.class));
        alarmConfigDto.setDangers(JSON.parseArray(alarmConfig.getDanger(), ConditionDto.class));
        alarmConfigDto.setSeveritys(JSON.parseArray(alarmConfig.getSeverity(), ConditionDto.class));
        return alarmConfigDto;
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
        alarmConfigQuartzService.deleteJob(ids);
    }

}
