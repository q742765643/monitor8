package com.piesat.skywalking.entity;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="T_MT_HOST_CONFIG")
public class HostConfigEntity extends BaseEntity {
    @Excel(name = "主机ip")
    @Column(name="ip", length=50)
    private String ip;

    @Excel(name = "主机hostName")
    @Column(name="host_name", length=50)
    private String hostName;

    @Excel(name = "主机类型")
    @Column(name="type", length=50)
    private String type;

    @Excel(name = "是否开启snmp")
    @Column(name="is_snmp", length=50)
    private int isSnmp;

    @Excel(name = "是否开启客户端")
    @Column(name="is_agent", length=50)
    private int isAgent;

    @Excel(name = "是否开启ssh")
    @Column(name="is_ssh", length=50)
    private int isSsh;

    @Excel(name = "操作系统类型")
    @Column(name="os", length=50)
    private String Os;

    @Excel(name = "ssh端口")
    @Column(name="ssh_port", length=50)
    private int sshPort;

    @Excel(name = "ssh 用户名")
    @Column(name="ssh_username", length=50)
    private int sshUserName;

    @Excel(name = "ssh密码")
    @Column(name="ssh_password", length=50)
    private int sshPassWord;
}
