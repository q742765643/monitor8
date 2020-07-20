package com.piesat.ucenter.dao.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.ucenter.entity.system.DictTypeEntity;
import org.springframework.stereotype.Repository;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 15:30
 */
@Repository
public interface DictTypeDao extends BaseDao<DictTypeEntity> {

    DictTypeEntity findByDictType(String dictType);
}
