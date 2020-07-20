package com.piesat.ucenter.rpc.dto.system;

import com.piesat.util.BaseDto;
import lombok.Data;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 15:38
 */
@Data
public class RoleDto extends BaseDto {
    /** 角色名称 */
    private String roleName;

    /** 角色权限 */
    private String roleKey;

    /** 角色排序 */
    private int roleSort;

    /** 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限） */
    private String dataScope;

    /** 角色状态（0正常 1停用） */
    private String status;

    /**
     * 备注
     */
    private String remark;

    private String[] menuIds;
    private String[] deptIds;


}
