package com.piesat.skywalking.handler;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("autoDiscoveryHandler")
public class AutoDiscoveryHandler implements BaseShardHandler {
    @GrpcHthtClient
    private HostConfigService hostConfigService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {

        String[] host = {".1.3.6.1.2.1.25.1.2.0"};
        String[] sw = {".1.3.6.1.2.1.17.4.3"};
        String[] router = {".1.3.6.1.2.1.4.1.0"};
        try {
            List<String> allIp = (List<String>) jobContext.getLists();
            List<String> ips = Ping.GetPingSuccess(allIp);

            for(String ip:ips){
                HostConfigDto hostConfig=new HostConfigDto();
                hostConfig.setIp(ip);
                SNMPSessionUtil dv = new SNMPSessionUtil(ip,"161", "public", "2");
                if (!"-1".equals(dv.getIsSnmpGet(PDU.GET,".1.3.6.1.2.1.1.5").get(0))) {
                    if (!"noSuchObject".equals(dv.getSnmpGet(PDU.GET,host).get(0))){//服务器
                        hostConfig.setType("server");
                    }else if ("1".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && dv.snmpWalk2(sw).isEmpty()){//路由器
                        hostConfig.setType("router");
                    }else if ("2".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && !dv.snmpWalk2(sw).isEmpty()) {//二层交换机
                        hostConfig.setType("linkSwitch");
                    }else if ("1".equals(dv.getSnmpGet(PDU.GET,router).get(0)) && !dv.snmpWalk2(sw).isEmpty()){//三层交换机
                        hostConfig.setType("networkSwitch");
                    }else {//未知设备
                        hostConfig.setType("unknownDevice");
                    }
                    String[] sysDesc = {SNMPConstants.SYSDESC};
                    ArrayList<String> sysDescs=dv.getSnmpGet(PDU.GET,sysDesc);
                    String[] sysName = {SNMPConstants.SYSNAME};
                    ArrayList<String> sysNames=dv.getSnmpGet(PDU.GET,sysName);
                    hostConfig.setHostName(sysNames.get(0));
                    hostConfig.setOs(sysDescs.get(0));
                    hostConfig.setIsSnmp("1");
                    hostConfig.setJobCron("0/30 * * * * ?");
                    hostConfig.setId(ip);
                    hostConfig.setTriggerStatus(1);
                    hostConfigService.save(hostConfig);

                }else {
                    hostConfig.setJobCron("0/30 * * * * ?");
                    hostConfig.setId(ip);
                    hostConfig.setTriggerStatus(0);
                    hostConfig.setType("unknownDevice");
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
        List<String>  exists=hostConfigService.selectNosnmp();
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
        String[] fields = new String[]{"host.os.name", "host.ip", "host.hostname"};
        searchSourceBuilder.fetchSource(fields,null);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(1);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search("metricbeat-*", searchSourceBuilder);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> map = hit.getSourceAsMap();
                Map<String, Object> host= (Map<String, Object>) map.get("host");
                Map<String, Object> os= (Map<String, Object>) host.get("os");
                hostConfigDto.setHostName((String) host.get("hostname"));
                hostConfigDto.setIsAgent("1");
                hostConfigDto.setOs((String) os.get("name"));
                hostConfigDto.setType("server");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
