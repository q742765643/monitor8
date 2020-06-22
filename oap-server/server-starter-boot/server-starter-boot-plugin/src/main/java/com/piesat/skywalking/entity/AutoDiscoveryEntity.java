package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="T_MT_AUTO_DISCOVERY")
public class AutoDiscoveryEntity extends BaseEntity {
}
