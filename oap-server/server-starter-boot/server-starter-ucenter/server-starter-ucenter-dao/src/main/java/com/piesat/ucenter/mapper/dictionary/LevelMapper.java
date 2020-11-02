package com.piesat.ucenter.mapper.dictionary;

import com.piesat.ucenter.entity.dictionary.LevelEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yaya
 * @description
 * @date 2019/12/23
 */
@Component
public interface LevelMapper {
    /**
     * 根据条件分页查询
     *
     * @param levelEntity
     * @return
     */
    public List<LevelEntity> selectLevelList(LevelEntity levelEntity);

    List<LevelEntity> getAllLevel();
}
