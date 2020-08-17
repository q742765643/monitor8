package com.piesat.skywalking.entity;

import com.piesat.common.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="T_MT_PACKET_LOSS_LOG")
public class PacketLossLogEntity extends BaseEntity {
    @Column(name="host_id", length=50)
    private String hostId;
    @Column(name="ip", length=50)
    private String ip;
    @Column(name="packet_loss", length=50)
    private float packetLoss;
}
