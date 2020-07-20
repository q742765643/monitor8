package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.MenuDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.util.RouterVo;
import com.piesat.ucenter.rpc.util.TreeSelect;
import com.piesat.util.constant.GrpcConstant;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Set;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 17:07
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface MenuService {
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<MenuDto> selectMenuList(MenuDto menu);
    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
   Set<String> selectMenuPermsByUserId(String userId);

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
   Set<String> getMenuPermission(UserDto user);
    /**
     * 获取路由列表
     *
     * @param userId 用户id
     * @return 获取路由
     */
   List<RouterVo> getRouters(String userId);
    /**
     *@描述 查询菜单select
     *@参数 [menu]
     *@返回值 java.util.List<com.piesat.ucenter.rpc.util.TreeSelect>
     *@author zzj
     *@创建时间 2019/11/29 15:51
     **/
   List<TreeSelect> treeSelect(MenuDto menu);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public MenuDto insertMenu(MenuDto menu);

    public void updateMenu(MenuDto menu);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    public MenuDto selectMenuById(String menuId);


    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    public void deleteMenuById(String menuId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    public List<String> selectMenuListByRoleId(String roleId);

    public void exportExcel(MenuDto menuDto);
}
