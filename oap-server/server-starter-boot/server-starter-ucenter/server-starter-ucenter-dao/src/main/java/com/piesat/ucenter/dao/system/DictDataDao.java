package com.piesat.ucenter.dao.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.ucenter.entity.system.DictDataEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 15:30
 */
@Repository
public interface DictDataDao extends BaseDao<DictDataEntity> {
    List<DictDataEntity> findByDictTypeAndStatusOrderByDictSortAsc(String dictType, String dictSort);

    DictDataEntity findByDictTypeAndDictValue(String dictType, String dictValue);
}
