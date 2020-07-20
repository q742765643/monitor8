package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.RoleDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Set;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 16:39
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface RoleService {

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByUserId(String userId);

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    Set<String> getRolePermission(UserDto user);

    /**
     * 根据条件分页查询角色数据
     *
     * @param pageForm 角色信息
     * @return 角色数据集合信息
     */
    public PageBean selectRoleList(PageForm<RoleDto> pageForm);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public RoleDto selectRoleById(String roleId);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public void insertRole(RoleDto role);

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public void authDataScope(RoleDto role);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    public void deleteRoleByIds(List<String> roleIds);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    public RoleDto updateRoleStatus(RoleDto role);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<RoleDto> selectRoleAll();

    public void exportExcel(RoleDto roleDto);
}
