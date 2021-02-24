package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @ClassName : EdgesEntity
 * @Description :
 * @Author : zzj
 * @Date: 2021-02-23 10:22
 */
@Entity
@Data
@Table(name = "T_MT_EDGES")
public class EdgesEntity extends BaseEntity {
    @Column(name = "source")
    private String source;
    @Column(name = "target")
    private String target;
    @Transient
    private Integer currentStatus;
}

