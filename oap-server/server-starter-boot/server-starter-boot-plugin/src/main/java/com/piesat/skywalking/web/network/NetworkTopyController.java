package com.piesat.skywalking.web.network;

import com.alibaba.fastjson.JSON;
import com.piesat.skywalking.api.discover.EdgesService;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.EdgesDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.ucenter.rpc.api.system.DictDataService;
import com.piesat.ucenter.rpc.dto.system.DictDataDto;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.PDU;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@Api(value = "网络拓扑接口", tags = {"网络拓扑接口"})
@RequestMapping("/networkTopy")
public class NetworkTopyController {
    @Autowired
    private HostConfigService hostConfigService;
    @Autowired
    private DictDataService dictDataService;
    @Autowired
    private EdgesService edgesService;

    @ApiOperation(value = "查询网络拓扑图", notes = "查询网络拓扑图")
    @GetMapping("/getTopy")
    public ResultT<Map<String, Object>> getTopy() {
        ResultT<Map<String, Object>> resultT = new ResultT<>();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> nodeList = new ArrayList<>();
        //List<Map<String, Object>> callList = new ArrayList<>();
        List<EdgesDto> edgesDtos=edgesService.selectAllWithHost();
        List<HostConfigDto> list = hostConfigService.selectAll();
        for (int i = 1; i <= list.size(); i++) {
            HostConfigDto hostConfig = list.get(i - 1);
            Map<String, Object> node = new HashMap<>();
            node.put("id", hostConfig.getId());
            node.put("ip", hostConfig.getIp());
            if (!"null".equals(String.valueOf(hostConfig.getArea()))) {
                node.put("comboId", String.valueOf(hostConfig.getArea()));
                node.put("cluster", String.valueOf(hostConfig.getArea()));
            }
            node.put("mediaType", hostConfig.getMediaType());
            node.put("area", hostConfig.getArea());
            nodeList.add(node);
        }
        map.put("nodes", nodeList);
        map.put("edges", edgesDtos);
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

    @ApiOperation(value = "查询连线", notes = "查询连线")
    @GetMapping("/selectBySource")
    public ResultT<List<String>> selectBySource(String source){
        ResultT<List<String>> resultT=new ResultT<>();
        List<String> list=edgesService.selectBySource(source);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "保存连线", notes = "保存连线")
    @PostMapping("/saveSource")
    public ResultT<String> saveSource(@RequestParam(value = "target[]") String target[], String source){
        ResultT<String> resultT=new ResultT<>();
        List<EdgesDto> edgesDtos=new ArrayList<>();
        for(int i=0;i<target.length;i++){
            EdgesDto edgesDto=new EdgesDto();
            edgesDto.setSource(source);
            edgesDto.setTarget(target[i]);
            edgesDtos.add(edgesDto);
        }
        edgesService.updateTopy(source,edgesDtos);
        return resultT;
    }

}
