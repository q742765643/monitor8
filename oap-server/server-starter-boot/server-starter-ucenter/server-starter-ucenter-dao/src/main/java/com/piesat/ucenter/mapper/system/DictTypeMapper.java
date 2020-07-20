package com.piesat.ucenter.mapper.system;

import com.piesat.ucenter.entity.system.DictTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 15:39
 */
@Component
public interface DictTypeMapper {
    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    public List<DictTypeEntity> selectDictTypeList(DictTypeEntity dictType);
}
