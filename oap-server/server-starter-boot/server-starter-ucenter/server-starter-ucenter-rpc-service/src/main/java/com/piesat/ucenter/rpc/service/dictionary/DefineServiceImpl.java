package com.piesat.ucenter.rpc.service.dictionary;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.jpa.specification.SimpleSpecificationBuilder;
import com.piesat.common.jpa.specification.SpecificationOperator;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.ucenter.dao.dictionary.DefineDao;
import com.piesat.ucenter.entity.dictionary.DefineEntity;
import com.piesat.ucenter.mapper.dictionary.DefineMapper;
import com.piesat.ucenter.rpc.api.dictionary.DefineService;
import com.piesat.ucenter.rpc.dto.dictionary.DefineDto;
import com.piesat.ucenter.rpc.mapstruct.dictionary.DefineMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yaya
 * @description 区域类别管理
 * @date 2019/12/26 17:21
 */
@Service
public class DefineServiceImpl extends BaseService<DefineEntity> implements DefineService {
    @Autowired
    private DefineDao defineDao;
    @Autowired
    private DefineMapstruct defineMapstruct;
    @Autowired
    private DefineMapper defineMapper;

    @Override
    public BaseDao<DefineEntity> getBaseDao() {
        return defineDao;
    }

    @Override
    public PageBean selectDefineList(PageForm<DefineDto> pageForm) {
        DefineEntity defineEntity=defineMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(),pageForm.getPageSize());
        //根据条件查询当前分页所有
        if(StringUtils.isNotBlank(defineEntity.getAreaId())){
            defineEntity.setAreaId("%"+defineEntity.getAreaId()+"%");
        }
        List<DefineEntity> defineEntities=defineMapper.selectDefineList(defineEntity);
        PageInfo<DefineEntity> pageInfo = new PageInfo<>(defineEntities);
        //获取当前页数据
        List<DefineDto> levelDtos= defineMapstruct.toDto(pageInfo.getList());
        PageBean pageBean=new PageBean(pageInfo.getTotal(),pageInfo.getPages(),levelDtos);
        return pageBean;
    }

    @Override
    public DefineDto saveDto(DefineDto defineDto) {
        DefineEntity defineEntity = this.defineMapstruct.toEntity(defineDto);
        defineEntity = this.saveNotNull(defineEntity);
        return this.defineMapstruct.toDto(defineEntity);
    }

    @Override
    public void deleteDefineByIds(List<String> defineIds) {
        this.deleteByIds(defineIds);
    }

    @Override
    public DefineDto updateDefine(DefineDto defineDto) {
        DefineEntity defineEntity = this.defineMapstruct.toEntity(defineDto);
        defineEntity = this.saveNotNull(defineEntity);
        return this.defineMapstruct.toDto(defineEntity);
    }

    @Override
    public DefineDto getDotById(String id) {
        DefineEntity defineEntity = this.getById(id);
        return this.defineMapstruct.toDto(defineEntity);
    }

    @Override
    public List<DefineDto> all() {
        List<DefineEntity> all = this.defineDao.findAll();
        return this.defineMapstruct.toDto(all);
    }

    @Override
    public void exportExcel(DefineDto defineDto) {
        List<DefineDto> dtoList = this.findByParam(defineDto);
        List<DefineEntity> entities=defineMapstruct.toEntity(dtoList);
        ExcelUtil<DefineEntity> util=new ExcelUtil(DefineEntity.class);
        util.exportExcel(entities,"区域类别管理");
    }

    @Override
    public List<DefineDto> findByParam(DefineDto defineDto) {
        SimpleSpecificationBuilder specificationBuilder=new SimpleSpecificationBuilder();
        if(com.piesat.common.utils.StringUtils.isNotNullString(defineDto.getAreaId())){
            specificationBuilder.add("areaId", SpecificationOperator.Operator.eq.name(),defineDto.getAreaId());
        }
        List<DefineEntity> defineEntities = this.getAll(specificationBuilder.generateSpecification());
        List<DefineDto> defineDtos = this.defineMapstruct.toDto(defineEntities);
        return defineDtos;
    }
}
