package com.piesat.skywalking.service.folder;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.folder.FileMonitorLogService;
import com.piesat.skywalking.dao.FileMonitorLogDao;
import com.piesat.skywalking.dto.DirectoryAccountDto;
import com.piesat.skywalking.dto.FileMonitorLogDto;
import com.piesat.skywalking.entity.DirectoryAccountEntity;
import com.piesat.skywalking.entity.FileMonitorLogEntity;
import com.piesat.skywalking.mapstruct.FileMonitorLogMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName : FileMonitorLogServiceImpl
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-26 09:52
 */
@Service
public class FileMonitorLogServiceImpl  extends BaseService<FileMonitorLogEntity> implements FileMonitorLogService {
    @Autowired
    private FileMonitorLogDao fileMonitorLogDao;
    @Autowired
    private FileMonitorLogMapstruct fileMonitorLogMapstruct;
    @Override
    public BaseDao<FileMonitorLogEntity> getBaseDao() {
        return fileMonitorLogDao;
    }

    public PageBean selectPageList(PageForm<FileMonitorLogDto> pageForm){
        FileMonitorLogEntity fileMonitorLogEntity=fileMonitorLogMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        pageBean.setPageData(fileMonitorLogMapstruct.toDto(pageBean.getPageData()));
        return pageBean;

    }
    @Override
    @Transactional
    public FileMonitorLogDto save(FileMonitorLogDto fileMonitorLogDto){

        FileMonitorLogEntity fileMonitorLogEntity=fileMonitorLogMapstruct.toEntity(fileMonitorLogDto);
        fileMonitorLogEntity=super.saveNotNull(fileMonitorLogEntity);
        return fileMonitorLogMapstruct.toDto(fileMonitorLogEntity);
    }

    @Override
    public FileMonitorLogDto findById(String id) {
        return fileMonitorLogMapstruct.toDto(super.getById(id));
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
    }
}

