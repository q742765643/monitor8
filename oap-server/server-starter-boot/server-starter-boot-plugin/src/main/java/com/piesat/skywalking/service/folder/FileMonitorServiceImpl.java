package com.piesat.skywalking.service.folder;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.dao.FileMonitorDao;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.entity.FileMonitorEntity;
import com.piesat.skywalking.mapstruct.FileMonitorMapstruct;
import com.piesat.skywalking.service.quartz.timing.FileMonitorQuartzService;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileMonitorServiceImpl extends BaseService<FileMonitorEntity> implements FileMonitorService {
    protected static final String REGEX = "(\\$\\{(.*?)\\})";
    protected static final Pattern PATTERN = Pattern.compile(REGEX);

    @Autowired
    private FileMonitorDao fileMonitorDao;
    @Autowired
    private FileMonitorMapstruct fileMonitorMapstruct;
    @Autowired
    private FileMonitorQuartzService fileMonitorQuartzService;
    @Autowired
    private AlarmEsLogService alarmEsLogService;

    @Override
    public BaseDao<FileMonitorEntity> getBaseDao() {
        return fileMonitorDao;
    }

    public PageBean selectPageList(PageForm<FileMonitorDto> pageForm) {
        FileMonitorEntity file = fileMonitorMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(file.getFolderRegular())) {
            specificationBuilder.addOr("folderRegular", SpecificationOperator.Operator.likeAll.name(), file.getFolderRegular());
        }
        if (StringUtils.isNotNullString(file.getFilenameRegular())) {
            specificationBuilder.addOr("filenameRegular", SpecificationOperator.Operator.likeAll.name(), file.getFilenameRegular());
        }
        if (StringUtils.isNotNullString(file.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), file.getTaskName());
        }
        if (StringUtils.isNotNullString((String) file.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) file.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) file.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) file.getParamt().get("endTime"));
        }
        if (null != file.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), file.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }

    public List<FileMonitorDto> selectBySpecification(FileMonitorDto fileMonitorDto) {
        FileMonitorEntity file = fileMonitorMapstruct.toEntity(fileMonitorDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(file.getFolderRegular())) {
            specificationBuilder.addOr("folderRegular", SpecificationOperator.Operator.likeAll.name(), file.getFolderRegular());
        }
        if (StringUtils.isNotNullString(file.getFilenameRegular())) {
            specificationBuilder.addOr("filenameRegular", SpecificationOperator.Operator.likeAll.name(), file.getFilenameRegular());
        }
        if (StringUtils.isNotNullString(file.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), file.getTaskName());
        }
        if (StringUtils.isNotNullString((String) file.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) file.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) file.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) file.getParamt().get("endTime"));
        }
        if (null != file.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), file.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        List<FileMonitorEntity> fileMonitorEntities = this.getAll(specification);
        return fileMonitorMapstruct.toDto(fileMonitorEntities);
    }

    @Override
    @Transactional
    public FileMonitorDto save(FileMonitorDto fileMonitorDto) {
        if (fileMonitorDto.getTriggerType() == null) {
            fileMonitorDto.setTriggerType(0);
        }
        if (fileMonitorDto.getTriggerStatus() == null) {
            fileMonitorDto.setTriggerStatus(0);
        }
        if (fileMonitorDto.getIsUt() == null) {
            fileMonitorDto.setIsUt(0);
        }
        if (fileMonitorDto.getIsUt() == 1) {
            fileMonitorDto.setDelayTime(8 * 60 * 60 * 1000);
        } else {
            fileMonitorDto.setDelayTime(0);
        }
        fileMonitorDto.setJobHandler("fileMonitorHandler");
        FileMonitorEntity fileMonitorEntity = fileMonitorMapstruct.toEntity(fileMonitorDto);
        fileMonitorEntity = super.saveNotNull(fileMonitorEntity);
        fileMonitorQuartzService.handleJob(fileMonitorMapstruct.toDto(fileMonitorEntity));
        return fileMonitorDto;
    }
    public FileMonitorDto updateFileMonitor(FileMonitorDto fileMonitorDto) {
        FileMonitorEntity fileMonitorEntity = fileMonitorMapstruct.toEntity(fileMonitorDto);
        fileMonitorEntity = super.saveNotNull(fileMonitorEntity);
        fileMonitorQuartzService.handleJob(fileMonitorMapstruct.toDto(fileMonitorEntity));
        return fileMonitorDto;
    }
    @Override
    public FileMonitorDto findById(String id) {
        return fileMonitorMapstruct.toDto(super.getById(id));
    }

    @Override
    @Transactional
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
        fileMonitorQuartzService.deleteJob(ids);
        alarmEsLogService.deleteAlarm(ids);
    }

    public long selectCount(FileMonitorDto fileMonitorDto) {
        FileMonitorEntity file = fileMonitorMapstruct.toEntity(fileMonitorDto);
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(file.getFolderRegular())) {
            specificationBuilder.addOr("folderRegular", SpecificationOperator.Operator.likeAll.name(), file.getFolderRegular());
        }
        if (StringUtils.isNotNullString(file.getFilenameRegular())) {
            specificationBuilder.addOr("filenameRegular", SpecificationOperator.Operator.likeAll.name(), file.getFilenameRegular());
        }
        if (StringUtils.isNotNullString(file.getTaskName())) {
            specificationBuilder.addOr("taskName", SpecificationOperator.Operator.likeAll.name(), file.getTaskName());
        }
        if (StringUtils.isNotNullString((String) file.getParamt().get("beginTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.ges.name(), (String) file.getParamt().get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) file.getParamt().get("endTime"))) {
            specificationBuilder.add("createTime", SpecificationOperator.Operator.les.name(), (String) file.getParamt().get("endTime"));
        }
        if (null != file.getTriggerStatus()) {
            specificationBuilder.add("triggerStatus", SpecificationOperator.Operator.eq.name(), file.getTriggerStatus());
        }
        Specification specification = specificationBuilder.generateSpecification();
        return super.count(specification);
    }


    public boolean regularCheck(FileMonitorDto fileMonitorDto) {
        String regular = fileMonitorDto.getFolderRegular() + "/" + fileMonitorDto.getFilenameRegular();
        Matcher m = PATTERN.matcher(regular);
        while (m.find()) {
            String expression = m.group(2);
            String replaceMent = expression.replaceAll("[ymdhsYMDHS]", "\\\\d");
            ;
            regular = regular.replace("${" + expression + "}", replaceMent);
        }
        regular = regular.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
        Pattern pattern = Pattern.compile(regular);
        Matcher matcher = pattern.matcher(fileMonitorDto.getFileSample());
        return matcher.matches();
    }

    public void trigger(String id){
        fileMonitorQuartzService.trigger(this.findById(id));
    }

}
