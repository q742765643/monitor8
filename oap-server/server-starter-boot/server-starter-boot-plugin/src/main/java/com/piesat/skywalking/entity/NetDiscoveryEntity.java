package com.piesat.skywalking.entity;

import com.piesat.common.annotation.Excel;
import com.piesat.skywalking.model.HtJobInfo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName : NetDiscovery
 * @Description :
 * @Author : zzj
 * @Date: 2021-03-29 09:46
 */
@Entity
@Data
@Table(name = "T_MT_NET_DISCOVERY")
@DiscriminatorValue("NETDISCOVERY")
public class NetDiscoveryEntity extends HtJobInfo {
    @Excel(name = "ip范围")
    @Column(name = "ip_range", columnDefinition = "TEXT")
    private String ipRange;

    @Excel(name = "更新间隔")
    @Column(name = "update_interval", length = 100)
    private String updateInterval;
}

