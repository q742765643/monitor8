package com.piesat.ucenter.entity.system;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户角色审核
 *
 * @author wlg
 * @description
 * @date 2020年2月19日上午10:44:39
 */
@Entity
@Table(name = "T_SOD_PORTAL_AUZ")
@Data
public class PortalAuzEntity extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * portal登录名
     */
    @Column(name = "ACCOUNT", length = 50)
    private String account;
    /**
     * 岗位
     */
    @Column(name = "POST", length = 200)
    private String post;
    /**
     * 用户中文名
     */
    @Column(name = "USERNAME", length = 100)
    private String username;
    /**
     * 审核状态
     */
    @Column(name = "STATUS", length = 2)
    private String status;


}
