package com.piesat.ucenter.rpc.service.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.ucenter.dao.system.DictDataDao;
import com.piesat.ucenter.entity.system.DictDataEntity;
import com.piesat.ucenter.entity.system.RoleEntity;
import com.piesat.ucenter.entity.system.UserEntity;
import com.piesat.ucenter.mapper.system.DictDataMapper;
import com.piesat.ucenter.mapper.system.DictTypeMapper;
import com.piesat.ucenter.rpc.api.system.DictDataService;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.ucenter.rpc.dto.system.RoleDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.mapstruct.system.DictDataMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 17:43
 */
@Service
public class DictDataServiceImpl extends BaseService<DictDataEntity> implements DictDataService {
    @Autowired
    private DictDataDao dictDataDao;
    @Autowired
    private DictDataMapstruct dictDataMapstruct;
    @Autowired
    private DictDataMapper dictDataMapper;
    @Override
    public BaseDao<DictDataEntity> getBaseDao() {
        return dictDataDao;
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param pageForm 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public PageBean selectDictDataList(PageForm<DictDataDto> pageForm)
    {
        DictDataEntity dictDataEntity=dictDataMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(),pageForm.getPageSize());
        List<DictDataEntity> dictDataEntities=dictDataMapper.selectDictDataList(dictDataEntity);
        PageInfo<DictDataEntity> pageInfo = new PageInfo<>(dictDataEntities);
        List<DictDataDto> userDtos= dictDataMapstruct.toDto(pageInfo.getList());
        PageBean pageBean=new PageBean(pageInfo.getTotal(),pageInfo.getPages(),userDtos);
        return pageBean;
    }
    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public DictDataDto selectDictDataById(String dictCode)
    {
        DictDataEntity dictDataEntity=this.getById(dictCode);
        return dictDataMapstruct.toDto(dictDataEntity);
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<DictDataDto> selectDictDataByType(String dictType)
    {
        List<DictDataEntity> dictDataEntities=dictDataDao.findByDictTypeAndStatusOrderByDictSortAsc(dictType,"0");
        return dictDataMapstruct.toDto(dictDataEntities);
    }


    /**
     * 通过字典ID删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    @Override
    public void deleteDictDataById(String dictCode)
    {
        this.delete(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    @Override
    public void deleteDictDataByIds(List<String> dictCodes)
    {
        this.deleteByIds(dictCodes);
    }

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public DictDataDto insertDictData(DictDataDto dictData)
    {
        DictDataEntity dictDataEntity=dictDataMapstruct.toEntity(dictData);
        dictDataEntity=this.saveNotNull(dictDataEntity);
        return dictDataMapstruct.toDto(dictDataEntity);
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public DictDataDto updateDictData(DictDataDto dictData)
    {
        DictDataEntity dictDataEntity=dictDataMapstruct.toEntity(dictData);
        dictDataEntity=this.saveNotNull(dictDataEntity);
        return dictDataMapstruct.toDto(dictDataEntity);
    }

    @Override
    public DictDataDto findByDictTypeAndDictValue(String dictType, String dictValue) {
        return dictDataMapstruct.toDto(this.dictDataDao.findByDictTypeAndDictValue(dictType,dictValue));
    }
    @Override
    public void exportExcel(DictDataDto dictData){
        DictDataEntity dictDataEntity=dictDataMapstruct.toEntity(dictData);
        List<DictDataEntity> entities=dictDataMapper.selectDictDataList(dictDataEntity);
        ExcelUtil<DictDataEntity> util=new ExcelUtil(DictDataEntity.class);
        util.exportExcel(entities,"字典数据信息");
    }
}
