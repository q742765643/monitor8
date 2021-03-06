package com.piesat.skywalking.service.folder;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.dao.FileMonitorDao;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.entity.FileMonitorEntity;
import com.piesat.skywalking.mapstruct.FileMonitorMapstruct;
import com.piesat.skywalking.service.quartz.timing.FileMonitorQuartzService;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
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
        this.computingTime(fileMonitorDto);
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

    public List<FileMonitorDto> selectAll(){
        return fileMonitorMapstruct.toDto(super.getAll());
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


    public boolean regularCheck(FileMonitorDto fileMonitorDto, ResultT<String> resultT) {
    /*    if(fileMonitorDto.getFileNum()==1){
            return true;
        }*/
        //String regular = fileMonitorDto.getFolderRegular() + "/" + fileMonitorDto.getFilenameRegular();
        String regular = fileMonitorDto.getFilenameRegular();
        Matcher m = PATTERN.matcher(regular);
        while (m.find()) {
            String expression = m.group(2);
            String replaceMent = expression.split(",")[0].replaceAll("[ymdhsYMDHS]", "\\\\d");
            regular = regular.replace("${" + expression + "}", replaceMent);
        }
        regular = regular.replace("?", "[\\s\\S]{1}").replace("*", "[\\s\\S]*");
        Pattern pattern = Pattern.compile(regular);
        Matcher matcher = pattern.matcher(fileMonitorDto.getFileSample());

        if (matcher.find( )) {
            resultT.setMessage("正则匹配通过:提取到内容"+matcher.group(0) );
        }else {
            resultT.setErrorMessage("正则校验不通过请重新配置");
        }
        //resultT.setMessage(resultT.getMsg()+"</br>实际正则表达式为:"+regular);
        return true;
    }

    public void trigger(String id){
        fileMonitorQuartzService.trigger(this.findById(id));
    }

    public void computingTime(FileMonitorDto fileMonitorDto){
        long time=0;
        if("D".equals(fileMonitorDto.getDelayStartUnit())){
            time=1000*60*60*24*fileMonitorDto.getDelayStart();
        }
        if("H".equals(fileMonitorDto.getDelayStartUnit())){
            time=1000*60*60*fileMonitorDto.getDelayStart();
        }
        if("M".equals(fileMonitorDto.getDelayStartUnit())){
            time=1000*60*fileMonitorDto.getDelayStart();
        }
        if("S".equals(fileMonitorDto.getDelayStartUnit())){
            time=1000*fileMonitorDto.getDelayStart();
        }
        fileMonitorDto.setDelayTime(fileMonitorDto.getDelayTime()+time);
    }
    public void exportExcel(){
        ExcelUtil<FileMonitorEntity> util = new ExcelUtil(FileMonitorEntity.class);
        List<FileMonitorEntity> fileMonitorEntities=new ArrayList<>();
        FileMonitorEntity fileMonitorEntity=new FileMonitorEntity();
        fileMonitorEntity.setTaskName("样列");
        fileMonitorEntity.setFolderRegular("/sharedata/grapes");
        fileMonitorEntity.setFilenameRegular("Z_NAFP_C_BABJ_${yyyyMMddHH,-8H}0000_P_[\\w\\W]*");
        fileMonitorEntity.setFileSample("/sharedata/grapes/Z_NAFP_C_BABJ_20210203030000_P_NWPC-GRAPES-3KM-ORIG-02600.grb2");
        fileMonitorEntity.setFileNum(10);
        fileMonitorEntity.setFileSize(10);
        fileMonitorEntity.setIsUt(0);
        fileMonitorEntity.setJobCron("0 0 15,3 * * ?");
        fileMonitorEntity.setDelayStart(1);
        fileMonitorEntity.setDelayStartUnit("H");
        fileMonitorEntity.setRangeTime(0);
        fileMonitorEntity.setRangeUnit("M");
        fileMonitorEntities.add(fileMonitorEntity);
        util.exportExcel(fileMonitorEntities, "文件配置-批量导入模板");
    }
    public void uploadExcel(InputStream inputStream){
        ExcelUtil<FileMonitorEntity> util = new ExcelUtil(FileMonitorEntity.class);
        try {
            List<FileMonitorEntity> fileMonitorEntities = util.importExcel(inputStream);
            for(int i=0;i<fileMonitorEntities.size();i++){
                fileMonitorEntities.get(i).setScanType(1);
                fileMonitorEntities.get(i).setTriggerStatus(0);
            }
            super.save(fileMonitorEntities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
