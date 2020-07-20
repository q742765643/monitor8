package com.piesat.ucenter.rpc.service.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.StringUtils;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.ucenter.common.constant.UserConstants;
import com.piesat.ucenter.dao.system.DictTypeDao;
import com.piesat.ucenter.entity.system.DictDataEntity;
import com.piesat.ucenter.entity.system.DictTypeEntity;
import com.piesat.ucenter.entity.system.UserEntity;
import com.piesat.ucenter.mapper.system.DictDataMapper;
import com.piesat.ucenter.mapper.system.DictTypeMapper;
import com.piesat.ucenter.rpc.api.system.DictTypeService;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.ucenter.rpc.dto.system.DictTypeDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.mapstruct.system.DictTypeMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 15:31
 */
@Service
public class DictTypeServiceImpl extends BaseService<DictTypeEntity> implements DictTypeService {
    @Autowired
    private DictTypeDao dictTypeDao;
    @Autowired
    private DictTypeMapper dictTypeMapper;
    @Autowired
    private DictTypeMapstruct dictTypeMapstruct;
    @Autowired
    private DictDataMapper dictDataMapper;
    @Override
    public BaseDao<DictTypeEntity> getBaseDao() {
        return dictTypeDao;
    }
    /**
     * 根据条件分页查询字典类型
     *
     * @param pageForm 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public PageBean selectDictTypeList(PageForm<DictTypeDto> pageForm)
    {
        DictTypeEntity dictType=dictTypeMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(),pageForm.getPageSize());
        List<DictTypeEntity> dictTypeEntities=dictTypeMapper.selectDictTypeList(dictType);
        PageInfo<DictTypeEntity> pageInfo = new PageInfo<>(dictTypeEntities);
        List<DictTypeDto> dictTypeDtos= dictTypeMapstruct.toDto(pageInfo.getList());
        PageBean pageBean=new PageBean(pageInfo.getTotal(),pageInfo.getPages(),dictTypeDtos);
        return pageBean;
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<DictTypeDto> selectDictTypeAll()
    {
        List<DictTypeEntity> dictTypeEntities=this.getAll();
        return dictTypeMapstruct.toDto(dictTypeEntities);
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public DictTypeDto selectDictTypeById(String dictId)
    {
        DictTypeEntity dictTypeEntity=this.getById(dictId);
        return dictTypeMapstruct.toDto(dictTypeEntity);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    public DictTypeDto selectDictTypeByType(String dictType)
    {
        DictTypeEntity dictTypeEntity=dictTypeDao.findByDictType(dictType);
        return dictTypeMapstruct.toDto(dictTypeEntity);    }

    /**
     * 通过字典ID删除字典信息
     *
     * @param dictId 字典ID
     * @return 结果
     */
    @Override
    public void deleteDictTypeById(String dictId)
    {
       this.delete(dictId);
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    @Override
    public void deleteDictTypeByIds(List<String> dictIds)
    {
        this.deleteByIds(dictIds);
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    public DictTypeDto insertDictType(DictTypeDto dictType)
    {
        DictTypeEntity dictTypeEntity=dictTypeMapstruct.toEntity(dictType);
        return dictTypeMapstruct.toDto(this.saveNotNull(dictTypeEntity));
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public DictTypeDto updateDictType(DictTypeDto dictType)
    {
        DictTypeEntity oldDict = this.getById(dictType.getId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        DictTypeEntity newDict=this.saveNotNull(dictTypeMapstruct.toEntity(dictType));
        return dictTypeMapstruct.toDto(newDict);
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public String checkDictTypeUnique(DictTypeDto dict)
    {
        DictTypeEntity dictType = dictTypeDao.findByDictType(dict.getDictType());
        if (StringUtils.isNotNull(dictType))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void exportExcel(DictTypeDto dictType){
        DictTypeEntity dictTypeEntity=dictTypeMapstruct.toEntity(dictType);
        List<DictTypeEntity> entities=dictTypeMapper.selectDictTypeList(dictTypeEntity);
        ExcelUtil<DictTypeEntity> util=new ExcelUtil(DictTypeEntity.class);
        util.exportExcel(entities,"字典类型信息");
    }

}
