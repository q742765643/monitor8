package com.piesat.skywalking.entity;

import com.piesat.common.annotation.Excel;
import com.piesat.skywalking.model.HtJobInfo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "T_MT_AUTO_DISCOVERY")
@DiscriminatorValue("DISCOVERY")
public class AutoDiscoveryEntity extends HtJobInfo {

    @Excel(name = "ip范围")
    @Column(name = "ip_range", columnDefinition = "TEXT")
    private String ipRange;

    @Excel(name = "更新间隔")
    @Column(name = "update_interval", length = 100)
    private String updateInterval;


}
