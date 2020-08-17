package com.piesat.skywalking.dto;

import com.piesat.util.BaseDto;
import lombok.Data;

@Data
public class PacketLossLogDto extends BaseDto {
    private String hostId;

    private String ip;

    private float packetLoss;
}
