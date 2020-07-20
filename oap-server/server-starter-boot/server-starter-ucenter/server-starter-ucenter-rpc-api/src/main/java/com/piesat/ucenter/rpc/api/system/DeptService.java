package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.DeptDto;
import com.piesat.ucenter.rpc.util.TreeSelect;
import com.piesat.util.constant.GrpcConstant;

import java.util.List;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/3 9:42
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface DeptService {
    /**
     * 查询部门管理数据
     *
     * @param deptDto 部门信息
     * @return 部门信息集合
     */
    public List<DeptDto> selectDeptList(DeptDto deptDto);

    /**
     *@描述 获取部门下拉树列表
     *@参数 [deptDto]
     *@返回值 java.util.List<com.piesat.ucenter.rpc.util.TreeSelect>
     *@author zzj
     *@创建时间 2019/12/3 10:43
     **/
    public List<TreeSelect> getTreeSelectDept(DeptDto deptDto);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public DeptDto selectDeptById(String deptId);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public DeptDto insertDept(DeptDto dept);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public DeptDto updateDept(DeptDto dept);


    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public void deleteDeptById(String deptId);
}
