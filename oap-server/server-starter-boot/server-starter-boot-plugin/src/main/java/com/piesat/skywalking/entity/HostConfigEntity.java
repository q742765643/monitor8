package com.piesat.skywalking.entity;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import com.piesat.skywalking.model.HtJobInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name="T_MT_HOST_CONFIG")
@DiscriminatorValue("HOSTCONFIG")
public class HostConfigEntity extends HtJobInfo {
    @Excel(name = "主机ip")
    @Column(name="ip", length=50)
    private String ip;

    @Excel(name = "主机hostName")
    @Column(name="host_name", length=100)
    private String hostName;

    @Excel(name = "主机类型")
    @Column(name="type", length=225)
    private String type;

    @Column(name="device_type", length=225)
    private Integer deviceType =-1;

    @Excel(name = "是否开启snmp")
    @Column(name="is_snmp", length=10)
    private String isSnmp;

    @Excel(name = "是否开启客户端")
    @Column(name="is_agent", length=10)
    private String isAgent;

    @Excel(name = "是否开启ssh")
    @Column(name="is_ssh", length=10)
    private String isSsh;

    @Excel(name = "操作系统类型")
    @Column(name="os", length=255)
    private String os;

    @Excel(name = "ssh端口")
    @Column(name="ssh_port", length=50)
    private int sshPort;

    @Excel(name = "ssh 用户名")
    @Column(name="ssh_username", length=50)
    private String sshUserName;

    @Excel(name = "ssh密码")
    @Column(name="ssh_password", length=50)
    private String sshPassWord;

    @Column(name="current_status", length=10)
    private Integer currentStatus=-1;

    @Transient
    private List<String> types;

}
