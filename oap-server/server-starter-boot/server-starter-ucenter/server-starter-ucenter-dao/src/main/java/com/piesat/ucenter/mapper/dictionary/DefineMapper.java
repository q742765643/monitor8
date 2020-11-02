package com.piesat.ucenter.mapper.dictionary;

import com.piesat.ucenter.entity.dictionary.DefineEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yaya
 * @description TODO
 * @date 2019/12/26 17:16
 */
@Component
public interface DefineMapper {
    /**
     * 根据条件分页查询
     *
     * @param defineEntity
     * @return
     */
    public List<DefineEntity> selectDefineList(DefineEntity defineEntity);
}
