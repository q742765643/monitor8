package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "T_MT_PROCESS_CONFIG")
public class ProcessConfigEntity extends BaseEntity {
    @Column(name = "ip", length = 255)
    private String ip;
    @Column(name = "process_name", length = 255)
    private String processName;
    @Column(name = "host_id", length = 255)
    private String hostId;
    @Column(name = "current_status", length = 10)
    private Integer currentStatus = -1;

    @Column(name = "cmdline", columnDefinition = "TEXT")
    private String cmdline;

    @Column(name = "pid")
    private Integer pid;

}
