package com.piesat.ucenter.mapper.system;

import com.piesat.ucenter.entity.system.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 16:00
 */
@Component
public interface RoleMapper {
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<RoleEntity> selectRoleList(RoleEntity role);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<RoleEntity> selectRolePermissionByUserId(String userId);

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    public List<String> selectRoleListByUserId(String userId);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    public List<RoleEntity> selectRolesByUserName(String userName);


}
