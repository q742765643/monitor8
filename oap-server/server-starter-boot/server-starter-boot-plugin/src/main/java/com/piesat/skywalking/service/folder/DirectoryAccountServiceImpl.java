package com.piesat.skywalking.service.folder;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.StringUtils;
import com.piesat.skywalking.api.folder.DirectoryAccountService;
import com.piesat.skywalking.dao.DirectoryAccountDao;
import com.piesat.skywalking.dto.DirectoryAccountDto;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.entity.DirectoryAccountEntity;
import com.piesat.skywalking.entity.FileMonitorEntity;
import com.piesat.skywalking.mapstruct.DirectoryAccountMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName : DirectoryAccountServiceImpl
 * @Description : 共享目录账号
 * @Author : zzj
 * @Date: 2020-10-26 09:44
 */
@Service
public class DirectoryAccountServiceImpl extends BaseService<DirectoryAccountEntity> implements DirectoryAccountService {
    @Autowired
    private DirectoryAccountDao directoryAccountDao;
    @Autowired
    private DirectoryAccountMapstruct directoryAccountMapstruct;
    @Override
    public BaseDao<DirectoryAccountEntity> getBaseDao() {
        return directoryAccountDao;
    }

    public PageBean selectPageList(PageForm<DirectoryAccountDto> pageForm){
        DirectoryAccountEntity directoryAccountEntity=directoryAccountMapstruct.toEntity(pageForm.getT());
        SimpleSpecificationBuilder specificationBuilder = new SimpleSpecificationBuilder();
        if (StringUtils.isNotNullString(directoryAccountEntity.getName())) {
            specificationBuilder.add("name", SpecificationOperator.Operator.likeAll.name(),directoryAccountEntity.getName());
        }

        Specification specification = specificationBuilder.generateSpecification();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageBean pageBean = this.getPage(specification, pageForm, sort);
        return pageBean;

    }
    public List<DirectoryAccountDto> selectAll(){
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return directoryAccountMapstruct.toDto(this.getAll(sort));
    }
    @Override
    @Transactional
    public DirectoryAccountDto save(DirectoryAccountDto directoryAccountDto){

        DirectoryAccountEntity directoryAccountEntity=directoryAccountMapstruct.toEntity(directoryAccountDto);
        directoryAccountEntity=super.saveNotNull(directoryAccountEntity);
        return directoryAccountMapstruct.toDto(directoryAccountEntity);
    }

    @Override
    public DirectoryAccountDto findById(String id) {
        return directoryAccountMapstruct.toDto(super.getById(id));
    }

    @Override
    public void deleteByIds(List<String> ids) {
        super.deleteByIds(ids);
    }
}

