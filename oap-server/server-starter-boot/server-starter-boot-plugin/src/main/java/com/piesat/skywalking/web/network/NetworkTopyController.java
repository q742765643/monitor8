package com.piesat.skywalking.web.network;

import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.entity.HostConfigEntity;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.service.host.HostConfigServiceImpl;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.snmp4j.PDU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "网络拓扑接口", tags = {"网络拓扑接口"})
@RequestMapping("/networkTopy")
public class NetworkTopyController {
    @Autowired
    private HostConfigService hostConfigService;

    @ApiOperation(value = "查询网络拓扑图", notes = "查询网络拓扑图")
    @GetMapping("/getTopy")
    public ResultT<Map<String, Object>> getTopy() {
        ResultT<Map<String, Object>> resultT = new ResultT<>();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> nodeList = new ArrayList<>();
        List<Map<String, Object>> callList = new ArrayList<>();
        Sort sort = Sort.by("ip");
        List<HostConfigDto> list = hostConfigService.selectAll();
        List<String> ipList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            HostConfigDto hostConfig = list.get(i);
            Map<String, Object> node = new HashMap<>();
            node.put("id", hostConfig.getId());
            node.put("name", hostConfig.getIp());
            node.put("type", hostConfig.getMediaType());
            node.put("isReal", true);
            nodeList.add(node);
            ipList.add(hostConfig.getIp());
        }
        String[] arpIp = {"1.3.6.1.2.1.4.22.1.3"};
        for (int i = 0; i < list.size(); i++) {
            HostConfigDto hostConfig = list.get(i);
            if (null != hostConfig.getMediaType() && (hostConfig.getMediaType()==2||hostConfig.getMediaType()==3)) {
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
            if (null != hostConfig.getMediaType() &&hostConfig.getMediaType()==4) {
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
        map.put("calls", callList);
        resultT.setData(map);
        return resultT;
    }
}
