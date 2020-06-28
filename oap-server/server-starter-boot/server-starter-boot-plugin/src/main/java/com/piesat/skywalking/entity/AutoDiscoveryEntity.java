package com.piesat.skywalking.entity;

import com.piesat.common.annotation.Excel;
import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="T_MT_AUTO_DISCOVERY")
public class AutoDiscoveryEntity extends BaseEntity {
    @Excel(name = "任务名称")
    @Column(name="dis_name", length=50)
    private String disName;

    @Excel(name = "ip范围")
    @Column(name="ip_range", length=100)
    private String ipRange;

    @Excel(name = "更新间隔")
    @Column(name="update_interval", length=100)
    private String updateInterval;

    @Excel(name = "cron表达式")
    @Column(name="cron", length=100)
    private String cron;

    @Excel(name = "状态")
    @Column(name="status", length=10)
    private String status;
}
