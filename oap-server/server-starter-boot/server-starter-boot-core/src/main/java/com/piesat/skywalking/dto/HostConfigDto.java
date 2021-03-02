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
    //@ApiModelProperty(value = "类型 server 服务器 router 路由 linkSwitch 二层交换机 networkSwitch 三层交换机 unknownDevice 未知")
    //private String type;
    @ApiModelProperty(value = "详细设备类型 11 未知 0 windows 1 linux 2 二层交换机 3 三层交换机 4 路由")
    private Integer mediaType;

    @ApiModelProperty(value = "设备类型 11 未知 0 服务器 1网络设备 2进程 3文件")
    private Integer deviceType;

    /*   @ApiModelProperty(value = "是否snmp 0 不是 1是")
       private String isSnmp;
       @ApiModelProperty(value = "是否代理 0 不是 1是")
       private String isAgent;
       private String isSsh;*/
    @ApiModelProperty(value = "监控方式 11 未知 1 代理 2 snmp 3 ping")
    private Integer monitoringMethods;
    @ApiModelProperty(value = "主机描述")
    private String os;
    /*    private int sshPort;
        private String sshUserName;
        private String sshPassWord;*/
    @ApiModelProperty(value = "设备当前状态 11 未知 0 一般 1 危险 2故障 3正常")
    private Integer currentStatus;
    @ApiModelProperty(value = "丢包率")
    private float packetLoss;
    @ApiModelProperty(value = "区域")
    private Integer area;
    @ApiModelProperty(value = "地址")
    private String location;
    @ApiModelProperty(value = "mac地址")
    private String mac;
    @ApiModelProperty(value = "掩码")
    private String mask;
    @ApiModelProperty(value = "网关")
    private String gateway;

    @ApiModelProperty(value = "平均cpu使用率")
    private float avgCpuPct;
    @ApiModelProperty(value = "最大cpu使用率")
    private float maxCpuPct;
    @ApiModelProperty(value = "平均内存使用率")
    private float avgMemoryPct;
    @ApiModelProperty(value = "最大内存使用率")
    private float maxMemoryPct;
    @ApiModelProperty(value = "最大文件使用率")
    private float maxFilesystemPct;
    @ApiModelProperty(value = "最大进程数")
    private long maxProcessSize;
    @ApiModelProperty(value = "平均丢包率")
    private float avgPacketPct;
    @ApiModelProperty(value = "最大丢包率")
    private float maxPacketPct;
    @ApiModelProperty(value = "总在线时间长")
    private long maxUptime;
    @ApiModelProperty(value = "是否主机 1 是 0否")
    private Integer isHost;
    @ApiModelProperty(value = "告警次数")
    private long alarmCount;
    @ApiModelProperty(value = "宕机次数")
    private long downCount;
    @ApiModelProperty(value = "宕机时长")
    private float downTime;
    private List<Integer> mediaTypes;

    private String alarmChart;

    private String useageChart;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "ip列表")
    private String[] ips;
}
