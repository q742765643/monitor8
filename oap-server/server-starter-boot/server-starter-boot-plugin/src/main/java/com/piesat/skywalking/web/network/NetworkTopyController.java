package com.piesat.skywalking.web.network;

import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.ucenter.rpc.api.system.DictDataService;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.snmp4j.PDU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@Api(value = "网络拓扑接口", tags = {"网络拓扑接口"})
@RequestMapping("/networkTopy")
public class NetworkTopyController {
    @Autowired
    private HostConfigService hostConfigService;
    @Autowired
    private DictDataService dictDataService;

    @ApiOperation(value = "查询网络拓扑图", notes = "查询网络拓扑图")
    @GetMapping("/getTopy")
    public ResultT<Map<String, Object>> getTopy() {
        ResultT<Map<String, Object>> resultT = new ResultT<>();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> nodeList = new ArrayList<>();
        List<Map<String, Object>> callList = new ArrayList<>();
        List<Map<String, Object>> groupList = new ArrayList<>();
        Sort sort = Sort.by("ip");
        List<HostConfigDto> list = hostConfigService.selectAll();
        List<String> ipList = new ArrayList<>();
        List<DictDataDto> dictDataDtoList = dictDataService.selectDictDataByType("media_area");
        int x = 100;
        int y = 100;
        Set<String> areaIds = new HashSet<>();
        for (int i = 1; i <= list.size(); i++) {
            HostConfigDto hostConfig = list.get(i - 1);
            Map<String, Object> node = new HashMap<>();
            areaIds.add(String.valueOf(hostConfig.getArea()));
            node.put("id", hostConfig.getId());
            node.put("ip", hostConfig.getIp());
            if (!"null".equals(String.valueOf(hostConfig.getArea()))) {
                node.put("comboId", String.valueOf(hostConfig.getArea()));
                node.put("cluster", String.valueOf(hostConfig.getArea()));
            }
            node.put("mediaType", hostConfig.getMediaType());
            node.put("area", hostConfig.getArea());
         /*   node.put("x", x);
            node.put("y", y);*/
            //node.put("isReal", true);
            nodeList.add(node);
            ipList.add(hostConfig.getIp());
        }
        for (int i = 0; i < dictDataDtoList.size(); i++) {
            DictDataDto dictDataDto = dictDataDtoList.get(i);
            Map<String, Object> group = new HashMap<>();
            if (!areaIds.contains(dictDataDto.getDictValue())) {
                continue;
            }
            group.put("id", String.valueOf(dictDataDto.getDictValue()));
            group.put("title", dictDataDto.getDictLabel());
            groupList.add(group);
        }
        String[] arpIp = {"1.3.6.1.2.1.4.22.1.3"};
        for (int i = 0; i < list.size(); i++) {
            HostConfigDto hostConfig = list.get(i);
            if (null != hostConfig.getMediaType() && (hostConfig.getMediaType() == 2 || hostConfig.getMediaType() == 3)) {
                SNMPSessionUtil dv = new SNMPSessionUtil(hostConfig.getIp(), "161", "public", "2");
                try {
                    if ("-1".equals(dv.getIsSnmpGet(PDU.GET, ".1.3.6.1.2.1.1.5").get(0))) {
                        continue;
                    }
                    ArrayList<String> arpList = dv.snmpWalk2(arpIp);
                    for (int j = 0; j < arpList.size(); j++) {
                        String ipAddr = arpList.get(j).split("=")[1].trim();
                        if (ipList.contains(ipAddr)) {
                            Map<String, Object> call = new HashMap<>();
                            call.put("id", hostConfig.getIp() + "-" + ipAddr);
                            call.put("source", hostConfig.getIp());
                            call.put("target", ipAddr);
                            callList.add(call);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dv.close();
            }
            if (null != hostConfig.getMediaType() && hostConfig.getMediaType() == 4) {
                SNMPSessionUtil dv = new SNMPSessionUtil(hostConfig.getIp(), "161", "public", "2");
                try {
                    String[] ipRouteNextHop = {".1.3.6.1.2.1.4.21.1.7"};//ipRouteNextHop
                    String[] ipRouteType = {".1.3.6.1.2.1.4.21.1.8"};//ipRouteType
                    if ("-1".equals(dv.getIsSnmpGet(PDU.GET, ".1.3.6.1.2.1.1.5").get(0))) {
                        continue;
                    }
                    ArrayList<String> ipRouteNextHopList = dv.snmpWalk2(ipRouteNextHop);
                    ArrayList<String> ipRouteTypeList = dv.snmpWalk2(ipRouteType);
                    Map<String, String> ipRouteMap = new HashMap<>();
                    for (int j = 0; j < ipRouteNextHopList.size(); j++) {
                        String ipAddr = ipRouteNextHopList.get(j).split("=")[1].trim();
                        String type = ipRouteTypeList.get(j).split("=")[1].trim();
                        if (!hostConfig.getIp().equals(ipAddr) && "4".equals(type)) {
                            ipRouteMap.put(ipAddr, ipAddr);
                        }
                    }
                    for (String key : ipRouteMap.keySet()) {
                        if (ipList.contains(key)) {
                            Map<String, Object> call = new HashMap<>();
                            call.put("id", hostConfig.getIp() + "-" + key);
                            call.put("source", hostConfig.getIp());
                            call.put("target", key);
                            callList.add(call);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dv.close();


            }
        }
        map.put("nodes", nodeList);
        map.put("edges", callList);
        //map.put("groups", groupList);
        resultT.setData(map);
        return resultT;
    }

    @ApiOperation(value = "查询监控总览", notes = "查询监控总览")
    @GetMapping("/findStateStatistics")
    public ResultT<List<Map<String,Object>>> findStateStatistics(){
        ResultT<List<Map<String,Object>>> resultT=new ResultT<>();
        List<Map<String,Object>> mapList=hostConfigService.findStateStatistics();
        resultT.setData(mapList);
        return resultT;
    }
}
