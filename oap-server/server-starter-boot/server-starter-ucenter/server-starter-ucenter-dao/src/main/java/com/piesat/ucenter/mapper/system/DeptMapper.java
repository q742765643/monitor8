package com.piesat.ucenter.mapper.system;

import com.piesat.ucenter.entity.system.DeptEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/27 18:26
 */
@Component
public interface DeptMapper {
    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<DeptEntity> selectDeptList(DeptEntity dept);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    public List<String> selectDeptListByRoleId(String roleId);

    /**
     * 根据ID查询
     *
     * @param deptId
     * @return
     */
    DeptEntity getById(@Param("deptId") String deptId);
}
