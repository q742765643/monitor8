package com.piesat.ucenter.rpc.service.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.StringUtils;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.ucenter.dao.system.RoleDao;
import com.piesat.ucenter.dao.system.RoleMenuDao;
import com.piesat.ucenter.entity.system.RoleEntity;
import com.piesat.ucenter.entity.system.RoleMenuEntity;
import com.piesat.ucenter.mapper.system.RoleMapper;
import com.piesat.ucenter.rpc.api.system.RoleService;
import com.piesat.ucenter.rpc.dto.system.RoleDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.mapstruct.system.RoleMapstruct;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 16:03
 */
@Service
public class RoleServiceImpl extends BaseService<RoleEntity> implements RoleService {
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMapstruct roleMapstruct;
    @Autowired
    private RoleMenuDao roleMenuDao;

    @Override
    public BaseDao<RoleEntity> getBaseDao() {
        return roleDao;
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param pageForm 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public PageBean selectRoleList(PageForm<RoleDto> pageForm) {
        RoleEntity roleEntity = roleMapstruct.toEntity(pageForm.getT());
        PageHelper.startPage(pageForm.getCurrentPage(), pageForm.getPageSize());
        List<RoleEntity> roleEntities = roleMapper.selectRoleList(roleEntity);
        PageInfo<RoleEntity> pageInfo = new PageInfo<>(roleEntities);
        List<RoleDto> roleDtos = roleMapstruct.toDto(pageInfo.getList());
        PageBean pageBean = new PageBean(pageInfo.getTotal(), pageInfo.getPages(), roleDtos);
        return pageBean;
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public RoleDto selectRoleById(String roleId) {
        RoleEntity roleEntity = this.getById(roleId);
        return roleMapstruct.toDto(roleEntity);
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public void insertRole(RoleDto role) {
        // 新增角色信息
        RoleEntity roleEntity = roleMapstruct.toEntity(role);
        roleEntity = this.saveNotNull(roleEntity);
        insertRoleMenu(roleEntity);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public void insertRoleMenu(RoleEntity role) {
        int rows = 1;
        // 新增用户与角色管理
        List<RoleMenuEntity> list = new ArrayList<>();
        for (String menuId : role.getMenuIds()) {
            RoleMenuEntity rm = new RoleMenuEntity();
            rm.setRoleId(role.getId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            roleMenuDao.saveNotNullAll(list);
        }
    }

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public void authDataScope(RoleDto role) {
        // 修改角色信息
        RoleEntity roleEntity = roleMapstruct.toEntity(role);
        roleEntity = this.saveNotNull(roleEntity);        // 删除角色与部门关联
        roleMenuDao.deleteByRoleId(roleEntity.getId());
        insertRoleMenu(roleEntity);
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(String userId) {
        List<RoleEntity> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (RoleEntity perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    @Override
    public Set<String> getRolePermission(UserDto user) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.getId().equals("1") || "api_manager".equals(user.getId())) {
            roles.add("admin");
        } else {
            roles.addAll(this.selectRolePermissionByUserId(user.getId()));
        }
        return roles;
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    public void deleteRoleByIds(List<String> roleIds) {
       /* for (Long roleId : roleIds)
        {
            checkRoleAllowed(new SysRole(roleId));
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0)
            {
                throw new CustomException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }*/
        for (String roleId : roleIds) {
            roleDao.deleteById(roleId);
        }
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public RoleDto updateRoleStatus(RoleDto role) {
        RoleEntity roleEntity = roleMapstruct.toEntity(role);
        roleEntity.setStatus(role.getStatus());
        roleEntity = this.saveNotNull(roleEntity);
        return roleMapstruct.toDto(roleEntity);
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<RoleDto> selectRoleAll() {
        List<RoleEntity> roleEntities = this.getAll();
        return roleMapstruct.toDto(roleEntities);
    }

    @Override
    public void exportExcel(RoleDto roleDto) {
        RoleEntity roleEntity = roleMapstruct.toEntity(roleDto);
        List<RoleEntity> entities = roleMapper.selectRoleList(roleEntity);
        ExcelUtil<RoleEntity> util = new ExcelUtil(RoleEntity.class);
        util.exportExcel(entities, "角色");
    }
}
