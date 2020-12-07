package com.piesat.ucenter.rpc.service.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.StringUtils;
import com.piesat.common.utils.poi.ExcelUtil;
import com.piesat.ucenter.dao.system.MenuDao;
import com.piesat.ucenter.entity.system.MenuEntity;
import com.piesat.ucenter.mapper.system.MenuMapper;
import com.piesat.ucenter.rpc.api.system.MenuService;
import com.piesat.ucenter.rpc.dto.system.MenuDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.ucenter.rpc.mapstruct.system.MenuMapstruct;
import com.piesat.ucenter.rpc.util.MetaVo;
import com.piesat.ucenter.rpc.util.RouterVo;
import com.piesat.ucenter.rpc.util.TreeSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 17:01
 */
@Service
public class MenuServiceImpl extends BaseService<MenuEntity> implements MenuService {
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private MenuMapstruct menuMapstruct;

    @Override
    public BaseDao<MenuEntity> getBaseDao() {
        return menuDao;
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<MenuDto> selectMenuList(MenuDto menu) {
        List<MenuEntity> menuList = menuMapper.selectMenuList(menuMapstruct.toEntity(menu));
        return this.buildMenuTree(menuMapstruct.toDto(menuList));
    }

    /**
     * @描述 查询菜单select
     * @参数 [menu]
     * @返回值 java.util.List<com.piesat.ucenter.rpc.util.TreeSelect>
     * @author zzj
     * @创建时间 2019/11/29 15:51
     **/
    @Override
    public List<TreeSelect> treeSelect(MenuDto menu) {
        List<MenuEntity> menuList = menuMapper.selectMenuList(menuMapstruct.toEntity(menu));
        return this.buildMenuTreeSelect(menuMapstruct.toDto(menuList));

    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildMenuTreeSelect(List<MenuDto> menus) {
        List<MenuDto> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<MenuDto> buildMenuTree(List<MenuDto> menus) {
        List<MenuDto> returnList = new ArrayList<MenuDto>();
        for (Iterator<MenuDto> iterator = menus.iterator(); iterator.hasNext(); ) {
            MenuDto t = (MenuDto) iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if ("0".equals(t.getParentId())) {
                recursionFn(menus, t);
                returnList.add(t);
            }
        }
        removeMenuTree(menus, returnList);
        if (!menus.isEmpty()) {
            returnList.addAll(menus);
        }
        return returnList;
    }

    /**
     * 菜单列表移除树结构列表中的节点
     *
     * @param menus    菜单列表
     * @param menuTree 树结构列表
     */
    public void removeMenuTree(List<MenuDto> menus, List<MenuDto> menuTree) {
        for (int i = 0; i < menuTree.size(); i++) {
            MenuDto t = menuTree.get(i);
            for (int j = 0; j < menus.size(); j++) {
                if (t.getId().equals(menus.get(j).getId())) {
                    menus.remove(menus.get(j));
                    break;
                }
            }
            List<MenuDto> children = t.getChildren();
            removeMenuTree(menus, children);
        }
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(String userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    @Override
    public Set<String> getMenuPermission(UserDto user) {
        Set<String> roles = new HashSet<String>();
        //roles.add("*:*:*");
        //String a="dm:databaseDefine:page";
        // 管理员拥有所有权限
        if (user.getId().equals("1"))
        {
            roles.add("*:*:*");
        }
        else
        {
            roles.addAll(this.selectMenuPermsByUserId(user.getId()));
        }
        return roles;
    }


    /**
     * 根据用户名称查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    public List<MenuDto> selectMenuTreeByUserId(String userId) {
        List<MenuEntity> menus = null;
        if ("1".equals(userId)) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menuMapstruct.toDto(menus), "0");
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<MenuDto> getChildPerms(List<MenuDto> list, String parentId) {
        List<MenuDto> returnList = new ArrayList<MenuDto>();
        for (Iterator<MenuDto> iterator = list.iterator(); iterator.hasNext(); ) {
            MenuDto t = (MenuDto) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId().equals(parentId)) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<MenuDto> list, MenuDto t) {
        // 得到子节点列表
        List<MenuDto> childList = getChildList(list, t);
        t.setChildren(childList);
        for (MenuDto tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<MenuDto> it = childList.iterator();
                while (it.hasNext()) {
                    MenuDto n = (MenuDto) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<MenuDto> getChildList(List<MenuDto> list, MenuDto t) {
        List<MenuDto> tlist = new ArrayList<MenuDto>();
        Iterator<MenuDto> it = list.iterator();
        while (it.hasNext()) {
            MenuDto n = (MenuDto) it.next();
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<MenuDto> list, MenuDto t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<MenuDto> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (MenuDto menu : menus) {
            RouterVo router = new RouterVo();
            router.setName(menu.getMenuName());
            router.setPath(getRouterPath(menu));
            router.setComponent(StringUtils.isEmpty(menu.getComponent()) ? "Layout" : menu.getComponent());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
            router.setName(menu.getMenuName());
            List<MenuDto> cMenus = menu.getChildren();
            //&& "M".equals(menu.getMenuType())
            if (!cMenus.isEmpty() && cMenus.size() > 0) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(MenuDto menu) {
        String routerPath = menu.getPath();
        // 非外链并且是一级目录
        if ("0".equals(menu.getParentId()) && 1 == menu.getIsFrame()) {
            routerPath = "/" + menu.getPath();
        }
        return routerPath;
    }

    @Override
    public List<RouterVo> getRouters(String userId) {
        List<MenuDto> menus = this.selectMenuTreeByUserId(userId);
        return this.buildMenus(menus);
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public MenuDto insertMenu(MenuDto menu) {
        MenuEntity menuEntity = menuMapstruct.toEntity(menu);
        return menuMapstruct.toDto(this.saveNotNull(menuEntity));
    }

    @Override
    public void updateMenu(MenuDto menu) {
        MenuEntity menuEntity = menuMapstruct.toEntity(menu);
        this.menuMapper.updateMenu(menuEntity);
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public MenuDto selectMenuById(String menuId) {
        MenuEntity menuEntity = this.getById(menuId);
        return menuMapstruct.toDto(menuEntity);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public void deleteMenuById(String menuId) {
        this.delete(menuId);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<String> selectMenuListByRoleId(String roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    @Override
    public void exportExcel(MenuDto menuDto) {
        MenuEntity menuEntity = menuMapstruct.toEntity(menuDto);
        List<MenuEntity> entities = menuMapper.selectMenuList(menuEntity);
        ExcelUtil<MenuEntity> util = new ExcelUtil(MenuEntity.class);
        util.exportExcel(entities, "菜单");
    }
}
