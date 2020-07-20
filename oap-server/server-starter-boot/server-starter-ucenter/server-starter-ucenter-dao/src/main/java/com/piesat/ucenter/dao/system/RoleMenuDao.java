package com.piesat.ucenter.dao.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.ucenter.entity.system.RoleMenuEntity;
import org.springframework.stereotype.Repository;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 15:48
 */
@Repository
public interface RoleMenuDao extends BaseDao<RoleMenuEntity> {

    void  deleteByRoleId(String roleId);
}
