package com.piesat.ucenter.entity.system;

import com.piesat.common.jpa.entity.UUIDEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/28 15:35
 */
@Entity
@Data
@Table(name="T_SOD_USER_ROLE")
public class UserRoleEntity extends UUIDEntity {
    /** 用户ID */
    @Column(name="USER_ID", length=32)
    private String userId;

    /** 角色ID */
    @Column(name="ROLE_ID", length=32)
    private String roleId;
}
