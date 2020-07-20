package com.piesat.ucenter.rpc.service.system;

import com.piesat.common.jpa.BaseDao;
import com.piesat.common.jpa.BaseService;
import com.piesat.common.utils.StringUtils;
import com.piesat.ucenter.dao.system.DeptDao;
import com.piesat.ucenter.entity.system.DeptEntity;
import com.piesat.ucenter.entity.system.MenuEntity;
import com.piesat.ucenter.mapper.system.DeptMapper;
import com.piesat.ucenter.rpc.api.system.DeptService;
import com.piesat.ucenter.rpc.dto.system.DeptDto;
import com.piesat.ucenter.rpc.mapstruct.system.DeptMapstruct;
import com.piesat.ucenter.rpc.util.TreeSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/27 19:40
 */
@Service
public class DeptServiceImpl extends BaseService<DeptEntity> implements DeptService {
    @Autowired
    private DeptDao deptDao;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private DeptMapstruct deptMapstruct;
    @Override
    public BaseDao<DeptEntity> getBaseDao() {
        return deptDao;
    }
    /**
     * 查询部门管理数据
     *
     * @param deptDto 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<DeptDto> selectDeptList(DeptDto deptDto)
    {
        DeptEntity dept=deptMapstruct.toEntity(deptDto);
        List<DeptEntity> deptEntities=deptMapper.selectDeptList(dept);
        List<DeptDto> deptDtos=this.buildDeptTree(deptMapstruct.toDto(deptEntities));
        return deptDtos;
    }
    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    public List<DeptDto> buildDeptTree(List<DeptDto> depts)
    {
        List<DeptDto> returnList = new ArrayList<DeptDto>();
        for (Iterator<DeptDto> iterator = depts.iterator(); iterator.hasNext();)
        {
            DeptDto t = (DeptDto) iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId().equals("0"))
            {
                recursionFn(depts, t);
                returnList.add(t);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }
    /**
     * 递归列表
     */
    private void recursionFn(List<DeptDto> list, DeptDto t) {
        // 得到子节点列表
        List<DeptDto> childList = getChildList(list, t);
        t.setChildren(childList);
        for (DeptDto tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                Iterator<DeptDto> it = childList.iterator();
                while (it.hasNext()) {
                    DeptDto n = (DeptDto) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }
    /**
     * 得到子节点列表
     */
    private List<DeptDto> getChildList(List<DeptDto> list, DeptDto t)
    {
        List<DeptDto> tlist = new ArrayList<DeptDto>();
        Iterator<DeptDto> it = list.iterator();
        while (it.hasNext())
        {
            DeptDto n = (DeptDto) it.next();
            if (n.getParentId().equals( t.getId()))
            {
                tlist.add(n);
            }
        }
        return tlist;
    }
    /**
     *@描述 获取部门下拉树列表
     *@参数 [deptDto]
     *@返回值 java.util.List<com.piesat.ucenter.rpc.util.TreeSelect>
     *@author zzj
     *@创建时间 2019/12/3 10:43 
     **/
    @Override
    public List<TreeSelect> getTreeSelectDept(DeptDto deptDto){
        DeptEntity dept=deptMapstruct.toEntity(deptDto);
        List<DeptEntity> deptEntities=deptMapper.selectDeptList(dept);
        List<TreeSelect> treeSelects=this.buildDeptTreeSelect(deptMapstruct.toDto(deptEntities));
        return treeSelects;
    }
    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<DeptDto> depts)
    {
        List<DeptDto> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    private boolean hasChild(List<DeptDto> list, DeptDto t)
    {
        return getChildList(list, t).size() > 0 ? true : false;
    }
    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */

    public List<String> selectDeptListByRoleId(String roleId)
    {
        return deptMapper.selectDeptListByRoleId(roleId);
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public DeptDto selectDeptById(String deptId)
    {
        DeptEntity deptEntity=this.getById(deptId);
        return deptMapstruct.toDto(deptEntity);
    }
    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public DeptDto insertDept(DeptDto dept)
    {
        DeptEntity info=this.getById(dept.getParentId());
        DeptEntity deptEntity=deptMapstruct.toEntity(dept);
        if(null!=info){
            deptEntity.setAncestors(info.getAncestors() + "," + dept.getParentId());
        }
        return deptMapstruct.toDto(this.saveNotNull(deptEntity));
    }
    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public DeptDto updateDept(DeptDto dept)
    {
        DeptEntity newParentDept = this.getById(dept.getParentId());
        DeptEntity oldDept =  this.getById(dept.getId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept))
        {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
        }
        DeptEntity deptEntity=deptMapstruct.toEntity(dept);
        return deptMapstruct.toDto(this.saveNotNull(deptEntity));
    }
    /**
     * 修改子元素关系
     *
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(String deptId, String newAncestors, String oldAncestors)
    {
        List<DeptEntity> children = deptDao.findByAncestorsLike(deptId);
        for (DeptEntity child : children)
        {
            child.setAncestors(child.getAncestors().replace(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            this.saveNotNull(children);
        }
    }
    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public void deleteDeptById(String deptId){
        this.delete(deptId);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public boolean hasChildByDeptId(String deptId)
    {
        int result = deptDao.countByIdAndDelFlag(deptId,"0");
        return result > 0 ? true : false;
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(String deptId)
    {
        int result = deptDao.countByIdAndDelFlag(deptId,"0");
        return result > 0 ? true : false;
    }
}
