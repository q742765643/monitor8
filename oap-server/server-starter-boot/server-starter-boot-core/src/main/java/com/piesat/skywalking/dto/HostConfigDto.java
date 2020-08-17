package com.piesat.skywalking.dto;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class HostConfigDto extends HtJobInfoDto {
    @ApiModelProperty(value = "主机ip")
    private String ip;
    @ApiModelProperty(value = "主机名称")
    private String hostName;
    @ApiModelProperty(value = "类型 server 服务器 router 路由 linkSwitch 二层交换机 networkSwitch 三层交换机 unknownDevice 未知")
    private String type;

    @ApiModelProperty(value = "设备类型 0 服务器 1网络设备 2进程 3文件")
    private Integer deviceType =-1;

    @ApiModelProperty(value = "是否snmp 0 不是 1是")
    private String isSnmp;
    @ApiModelProperty(value = "是否代理 0 不是 1是")
    private String isAgent;
    private String isSsh;
    @ApiModelProperty(value = "主机描述")
    private String os;
    private int sshPort;
    private String sshUserName;
    private String sshPassWord;
    @ApiModelProperty(value = "设备当前状态 0 一般 1 危险 2故障 3正常")
    private Integer currentStatus=-1;
    @ApiModelProperty(value = "丢包率")
    private float packetLoss;
    private List<String> types;
}
