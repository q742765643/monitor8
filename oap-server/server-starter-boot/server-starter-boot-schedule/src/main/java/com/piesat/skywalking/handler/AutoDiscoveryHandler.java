package com.piesat.skywalking.handler;

import com.alibaba.fastjson.JSON;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
import com.piesat.skywalking.handler.base.BaseShardHandler;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.util.GetRangeIpUtil;
import com.piesat.skywalking.util.Ping;
import com.piesat.skywalking.vo.NetworkVo;
import com.piesat.util.BaseDto;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.ResultT;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@Slf4j
@Service("autoDiscoveryHandler")
public class AutoDiscoveryHandler implements BaseShardHandler {
    @GrpcHthtClient
    private HostConfigService hostConfigService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {

       // String[] host = {".1.3.6.1.2.1.25.1.2.0"};

        String[] host = {".1.3.6.1.2.1.1.7.0"};
        String[] sw = {".1.3.6.1.2.1.17.4.3"};
        String[] router = {".1.3.6.1.2.1.4.1.0"};
        try {
            List<String> allIp = (List<String>) jobContext.getLists();
            List<String> ips = Ping.GetPingSuccess(allIp);

            for(String ip:ips){
                HostConfigDto hostConfig=new HostConfigDto();
                Integer mediaType=11;
                hostConfig.setMediaType(mediaType);
                hostConfig.setIp(ip);
                SNMPSessionUtil dv = new SNMPSessionUtil(ip,"161", "public", "2");
                if (!"-1".equals(dv.getIsSnmpGet(PDU.GET,".1.3.6.1.2.1.1.5").get(0))) {
                    System.out.println(dv.getSnmpGet(PDU.GET,host).get(0));
                    if (!"noSuchObject".equals(dv.getSnmpGet(PDU.GET,host).get(0))){//服务器
                        hostConfig.setDeviceType(0);
                        this.getMacAndGetaway(hostConfig,dv);
                    }else if ("1".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && dv.snmpWalk2(sw).isEmpty()){//路由器
                        hostConfig.setDeviceType(1);
                        mediaType=4;
                    }else if ("2".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && !dv.snmpWalk2(sw).isEmpty()) {//二层交换机
                        hostConfig.setDeviceType(1);
                        mediaType=2;
                    }else if ("1".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && !dv.snmpWalk2(sw).isEmpty()){//三层交换机
                        hostConfig.setDeviceType(1);
                        mediaType=3;
                    }else {//未知设备
                        hostConfig.setDeviceType(11);
                    }
                    this.getMediaType(hostConfig,dv,mediaType);
                    hostConfig.setIsSnmp("1");
                    hostConfig.setJobCron("0/30 * * * * ?");
                    hostConfig.setId(ip);
                    hostConfig.setTriggerStatus(1);
                    hostConfigService.save(hostConfig);

                }else {
                    hostConfig.setJobCron("0/30 * * * * ?");
                    hostConfig.setId(ip);
                    hostConfig.setTriggerStatus(0);
                    this.getHost(hostConfig);
                    hostConfigService.save(hostConfig);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT) {
        AutoDiscoveryDto autoDiscoveryDto= (AutoDiscoveryDto) jobContext.getHtJobInfoDto();
        List<String> ips = null;
        try {
            if(autoDiscoveryDto.getIpRange().indexOf("-")!=-1){
                ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange());
            }else{
                ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange(), 24);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String>  exists=hostConfigService.selectOnine();
        ips.removeAll(exists);
        return ips;
    }

    public void getHost(HostConfigDto hostConfigDto){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.cpu");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", hostConfigDto.getIp());
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        String[] fields = new String[]{"host.os.name",
                "host.ip", "host.hostname","host.ip","host.mask"
        };
        searchSourceBuilder.fetchSource(fields,null);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(1);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", searchSourceBuilder);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap,hit.getSourceAsString(),null);
                hostConfigDto.setHostName(String.valueOf(jsonMap.get("host.hostname")));
                hostConfigDto.setIsAgent("1");
                hostConfigDto.setOs(String.valueOf(jsonMap.get("host.os.name")));
                if(hostConfigDto.getOs().toUpperCase().indexOf("WINDOW")!=-1){
                    hostConfigDto.setMediaType(0);
                }
                if(hostConfigDto.getOs().toUpperCase().indexOf("LINUX")!=-1){
                    hostConfigDto.setMediaType(1);
                }
                hostConfigDto.setDeviceType(0);

                List<String>  macs= JSON.parseArray((String) jsonMap.get("host.mask"),String.class);
                for(int i=0;i<macs.size();i++){
                    String[] masks=macs.get(i).split("--");
                    if(macs.get(i).indexOf(hostConfigDto.getIp())!=-1){
                        if(masks.length>3){
                            hostConfigDto.setGateway(masks[3].trim());
                        }
                        hostConfigDto.setMask(masks[1].trim());
                        hostConfigDto.setMac(masks[2].trim());
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMediaType(HostConfigDto hostConfig,SNMPSessionUtil dv,Integer mediaType){

        try {
            String[] sysDesc = {SNMPConstants.SYSDESC};
            ArrayList<String> sysDescs = dv.getSnmpGet(PDU.GET, sysDesc);
            String[] sysName = {SNMPConstants.SYSNAME};
            ArrayList<String> sysNames = dv.getSnmpGet(PDU.GET, sysName);
            hostConfig.setHostName(sysNames.get(0));
            hostConfig.setOs(sysDescs.get(0));
            if(hostConfig.getDeviceType()==0){
              if(hostConfig.getOs().toUpperCase().indexOf("WINDOW")!=-1){
                  hostConfig.setMediaType(0);
              }
              if(hostConfig.getOs().toUpperCase().indexOf("LINUX")!=-1){
                  hostConfig.setMediaType(1);
              }
            }
            if(hostConfig.getDeviceType()==1){
                hostConfig.setMediaType(mediaType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMacAndGetaway(HostConfigDto hostConfig,SNMPSessionUtil dv){
        String[] maskOid = {
                ".1.3.6.1.2.1.4.20.1.1",".1.3.6.1.2.1.4.20.1.2",".1.3.6.1.2.1.4.20.1.3"
        };
        List<TableEvent> snmpWalk = dv.snmpWalk(maskOid);
        Map<String,String> indexMap=new HashMap<>();
        Map<String,String> maskMap=new HashMap<>();
        for(int i=0;i<snmpWalk.size();i++){
            TableEvent tableEvent=snmpWalk.get(i);
            VariableBinding[] values = tableEvent.getColumns();
            String ip=values[0].getVariable().toString();
            String index=values[1].getVariable().toString();
            String mask=values[2].getVariable().toString();
            indexMap.put(ip,index);
            maskMap.put(ip,mask);
        }
        if(!indexMap.containsKey(hostConfig.getIp())){
            return;
        }
        String index=indexMap.get(hostConfig.getIp());
        hostConfig.setMask(maskMap.get(hostConfig.getIp()));
        String[] macOid = {
                ".1.3.6.1.2.1.2.2.1.1",".1.3.6.1.2.1.2.2.1.6"
        };
        List<TableEvent> macWalk = dv.snmpWalk(macOid);
        for(int i=0;i<macWalk.size();i++) {
            TableEvent tableEvent = macWalk.get(i);
            VariableBinding[] values = tableEvent.getColumns();
            String indexMac=values[0].getVariable().toString();
            String mac=values[1].getVariable().toString();
            if(indexMac.equals(index)){
                hostConfig.setMac(mac);
                break;
            }
        }
        String[] getawayOid = {
                ".1.3.6.1.2.1.4.21.1.2",".1.3.6.1.2.1.4.21.1.7"
        };
        List<TableEvent> getawayWalk = dv.snmpWalk(getawayOid);
        for(int i=0;i<getawayWalk.size();i++) {
            TableEvent tableEvent = getawayWalk.get(i);
            VariableBinding[] values = tableEvent.getColumns();
            String indexMac=values[0].getVariable().toString();
            String getaWay=values[1].getVariable().toString();
            if(indexMac.equals(index)){
                hostConfig.setGateway(getaWay);
                break;
            }
        }

    }
}
