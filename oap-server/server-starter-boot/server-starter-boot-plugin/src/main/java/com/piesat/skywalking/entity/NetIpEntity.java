package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName : NetIpEntity
 * @Description :
 * @Author : zzj
 * @Date: 2021-03-29 10:15
 */
@Entity
@Data
@Table(name = "T_MT_NET_IP")
public class NetIpEntity extends BaseEntity {
    @Column(name = "ip", length = 50)
    private String ip;
    @Column(name = "current_status", length = 10)
    private Integer currentStatus = 0;
    @Column(name = "discovery_id", length = 50)
    private String discoveryId;
}

