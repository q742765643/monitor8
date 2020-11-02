package com.piesat.ucenter.dao.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.ucenter.entity.system.DeptEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/27 19:39
 */
@Repository
public interface DeptDao extends BaseDao<DeptEntity> {

    int countByIdAndDelFlag(String id, String delFlag);

    int countByParentIdAndDelFlag(String id, String delFlag);

    List<DeptEntity> findByAncestorsLike(String ancestors);

}
