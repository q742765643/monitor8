package com.piesat.skywalking.entity;

import com.piesat.common.annotation.Excel;
import com.piesat.skywalking.model.HtJobInfo;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "T_MT_HOST_CONFIG")
@DiscriminatorValue("HOSTCONFIG")
public class HostConfigEntity extends HtJobInfo {
    @Excel(name = "主机ip")
    @Column(name = "ip", length = 50)
    private String ip;

    @Excel(name = "主机hostName")
    @Column(name = "host_name", length = 100)
    private String hostName;

    @Column(name = "media_type", length = 100)
    private Integer mediaType;

    @Column(name = "device_type", length = 225)
    private Integer deviceType;

    @Column(name = "monitoring_methods", length = 2)
    private Integer monitoringMethods;

/*    @Excel(name = "是否开启snmp")
    @Column(name="is_snmp", length=10)
    private String isSnmp;

    @Excel(name = "是否开启客户端")
    @Column(name="is_agent", length=10)
    private String isAgent;

    @Excel(name = "是否开启ssh")
    @Column(name="is_ssh", length=10)
    private String isSsh;*/

    @Excel(name = "操作系统类型")
    @Column(name = "os", length = 255)
    private String os;

   /* @Excel(name = "ssh端口")
    @Column(name="ssh_port", length=50)
    private int sshPort;

    @Excel(name = "ssh 用户名")
    @Column(name="ssh_username", length=50)
    private String sshUserName;

    @Excel(name = "ssh密码")
    @Column(name="ssh_password", length=50)
    private String sshPassWord;*/

    @Column(name = "current_status", length = 10)
    private Integer currentStatus = -1;

    @Column(name = "packet_loss", length = 10)
    private float packetLoss;

    @Column(name = "area", length = 50)
    private Integer area;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "mac", length = 255)
    private String mac;
    @Column(name = "mask", length = 255)
    private String mask;

    @Column(name = "gateway", length = 255)
    private String gateway;
    @Column(name = "is_host", length = 255)
    private Integer isHost;

    @Transient
    private List<Integer> mediaTypes;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

}
