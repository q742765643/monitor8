package com.piesat.skywalking.excel;

import com.piesat.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName : HostTempVo
 * @Description :
 * @Author : zzj
 * @Date: 2021-03-03 17:40
 */
@Data
public class HostTempVo {
    @Excel(name = "设备别名")
    private String taskName;
    @Excel(name = "设备名称")
    private String hostName;
    @Excel(name = "设备类型",readConverterExp = "11=未知,0=windows,1=linux,2=二层交换机,3=三层交换机,4=路由",combo = {"未知","windows","linux","二层交换机","三层交换机","路由"})
    private Integer mediaType;
    @Excel(name = "ip")
    private String ip;
    @Excel(name = "监控方式",readConverterExp = "11=未知,1=代理,2=snmp,3=ping",combo = {"未知","代理","snmp","ping"})
    private Integer monitoringMethods;
    @Excel(name = "mac地址")
    private String mac;
    @Excel(name = "网关")
    private String gateway;
    @Excel(name = "区域",readConverterExp = "0=办公区,1=值班区,2=机房区,3=其他区",combo = {"办公区","值班区","机房区","其他区"})
    private Integer area;
    @Excel(name = "地址")
    private String location;
}

