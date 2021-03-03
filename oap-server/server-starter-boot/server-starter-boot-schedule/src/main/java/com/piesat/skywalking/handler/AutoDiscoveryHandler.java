package com.piesat.skywalking.handler;

import com.alibaba.fastjson.JSON;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseShardHandler;
import com.piesat.skywalking.om.protocol.snmp.SNMPConstants;
import com.piesat.skywalking.om.protocol.snmp.SNMPSessionUtil;
import com.piesat.skywalking.util.GetRangeIpUtil;
import com.piesat.skywalking.util.Ping;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.NullUtil;
import com.piesat.util.ResultT;
import com.piesat.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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

            for (String ip : ips) {
                HostConfigDto hostConfig = new HostConfigDto();
                hostConfig.setIp(ip);
                hostConfig.setCurrentStatus(3);
                hostConfig.setDeviceType(1);
                hostConfig.setMediaType(11);
                hostConfig.setIsHost(0);
                SNMPSessionUtil dv = new SNMPSessionUtil(ip, "161", "public", "2");
                try {

                    if (!"-1".equals(dv.getIsSnmpGet(PDU.GET, ".1.3.6.1.2.1.1.5").get(0))) {
                        if (!"noSuchObject".equals(dv.getSnmpGet(PDU.GET, host).get(0))) {//服务器
                            this.getMacAndGetaway(hostConfig, dv);
                            this.getMediaType(hostConfig, dv);
                        }
                        if (0 == hostConfig.getIsHost()) {
                            String isRouter = dv.getSnmpGet(PDU.GET, router).get(0);
                            boolean isSw = dv.snmpWalk2(sw).isEmpty();
                            if ("1".equals(isRouter) && isSw) {//路由器
                                hostConfig.setMediaType(4);
                            }
                            if ("2".equals(isRouter) && !isSw) {//二层交换机
                                hostConfig.setMediaType(2);
                            }
                            if ("1".equals(isRouter) && !isSw) {//三层交换机
                                hostConfig.setMediaType(3);
                            }
                        }
                        hostConfig.setMonitoringMethods(2);
                        hostConfig.setJobCron(new Random().nextInt(29)+"/30 * * * * ?");
                        //hostConfig.setId(ip);
                        hostConfig.setTriggerStatus(1);
                        //hostConfigService.save(hostConfig);
                        this.findHost(hostConfig);
                    } else {
                        hostConfig.setJobCron(new Random().nextInt(29)+"/30 * * * * ?");
                        //hostConfig.setId(ip);
                        hostConfig.setTriggerStatus(1);
                        hostConfig.setMonitoringMethods(3);
                        this.getHost(hostConfig);
                        this.findHost(hostConfig);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dv.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT) {
        AutoDiscoveryDto autoDiscoveryDto = (AutoDiscoveryDto) jobContext.getHtJobInfoDto();
        List<String> ips = null;
        try {
            if (autoDiscoveryDto.getIpRange().indexOf("-") != -1) {
                ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange());
            } else if(autoDiscoveryDto.getIpRange().indexOf(",") != -1){
                String[] ipStr=autoDiscoveryDto.getIpRange().split(",");
                ips=Arrays.asList(ipStr);
                //ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange(), 24);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> exists = hostConfigService.selectOnine();
        if (null != exists) {
            ips.removeAll(exists);
        }
        return ips;
    }

    public void getHost(HostConfigDto hostConfigDto) {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime=format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -60);
        String beginTime=format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system.cpu");
        MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", hostConfigDto.getIp());
        MatchQueryBuilder matchType = QueryBuilders.matchQuery("agent.type", "metricbeat");
        boolBuilder.must(matchType);
        boolBuilder.must(matchEvent);
        boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        String[] fields = new String[]{"host.os.name",
                "host.ip", "host.hostname", "host.ip", "host.mask"
        };
        searchSourceBuilder.fetchSource(fields, null);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(1);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT + "-*", searchSourceBuilder);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                hostConfigDto.setHostName(String.valueOf(jsonMap.get("host.hostname")));
                hostConfigDto.setMonitoringMethods(1);
                hostConfigDto.setOs(String.valueOf(jsonMap.get("host.os.name")));
                if (hostConfigDto.getOs().toUpperCase().indexOf("WINDOW") != -1) {
                    hostConfigDto.setMediaType(0);
                }
                if (hostConfigDto.getOs().toUpperCase().indexOf("LINUX") != -1) {
                    hostConfigDto.setMediaType(1);
                }
                List<String> macs = JSON.parseArray((String) jsonMap.get("host.mask"), String.class);
                for (int i = 0; i < macs.size(); i++) {
                    String[] masks = macs.get(i).split("--");
                    if (macs.get(i).indexOf(hostConfigDto.getIp()) != -1) {
                        if (masks.length > 3) {
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

    public void getMediaType(HostConfigDto hostConfig, SNMPSessionUtil dv) {

        try {
            String[] sysDesc = {SNMPConstants.SYSDESC};
            ArrayList<String> sysDescs = dv.getSnmpGet(PDU.GET, sysDesc);
            String[] sysName = {SNMPConstants.SYSNAME};
            ArrayList<String> sysNames = dv.getSnmpGet(PDU.GET, sysName);
            hostConfig.setHostName(sysNames.get(0));
            hostConfig.setOs(sysDescs.get(0));
            if (hostConfig.getOs().toUpperCase().indexOf("WINDOW") != -1) {
                hostConfig.setMediaType(0);
                hostConfig.setIsHost(1);
            }
            if (hostConfig.getOs().toUpperCase().indexOf("LINUX") != -1) {
                hostConfig.setMediaType(1);
                hostConfig.setIsHost(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMacAndGetaway(HostConfigDto hostConfig, SNMPSessionUtil dv) {
        String[] maskOid = {
                ".1.3.6.1.2.1.4.20.1.1", ".1.3.6.1.2.1.4.20.1.2", ".1.3.6.1.2.1.4.20.1.3"
        };
        List<TableEvent> snmpWalk = dv.snmpWalk(maskOid);
        Map<String, String> indexMap = new HashMap<>();
        Map<String, String> maskMap = new HashMap<>();
        for (int i = 0; i < snmpWalk.size(); i++) {
            TableEvent tableEvent = snmpWalk.get(i);
            VariableBinding[] values = tableEvent.getColumns();
            String ip = values[0].getVariable().toString();
            String index = values[1].getVariable().toString();
            String mask = values[2].getVariable().toString();
            indexMap.put(ip, index);
            maskMap.put(ip, mask);
        }
        String index = "";
        if (!indexMap.containsKey(hostConfig.getIp())) {
            for (Map.Entry<String, String> entry : indexMap.entrySet()) {
                if ("2".equals(entry.getValue())) {
                    index = "2";
                    hostConfig.setMask(maskMap.get(entry.getKey()));
                }
            }
            ;
        } else {
            index = indexMap.get(hostConfig.getIp());
            hostConfig.setMask(maskMap.get(hostConfig.getIp()));
        }
        if ("".equals(index)) {
            return;
        }

        String[] macOid = {
                ".1.3.6.1.2.1.2.2.1.1", ".1.3.6.1.2.1.2.2.1.6"
        };
        List<TableEvent> macWalk = dv.snmpWalk(macOid);
        for (int i = 0; i < macWalk.size(); i++) {
            TableEvent tableEvent = macWalk.get(i);
            VariableBinding[] values = tableEvent.getColumns();
            String indexMac = values[0].getVariable().toString();
            String mac = values[1].getVariable().toString();
            if (indexMac.equals(index)) {
                hostConfig.setMac(mac);
                break;
            }
        }
        String[] getawayOid = {
                ".1.3.6.1.2.1.4.21.1.2", ".1.3.6.1.2.1.4.21.1.7"
        };
        List<TableEvent> getawayWalk = dv.snmpWalk(getawayOid);
        for (int i = 0; i < getawayWalk.size(); i++) {
            TableEvent tableEvent = getawayWalk.get(i);
            VariableBinding[] values = tableEvent.getColumns();
            String indexMac = values[0].getVariable().toString();
            String getaWay = values[1].getVariable().toString();
            if (indexMac.equals(index)) {
                hostConfig.setGateway(getaWay);
                break;
            }
        }

    }
    public void findHost(HostConfigDto hostConfig){
        HostConfigDto qh=new HostConfigDto();
        NullUtil.changeToNull(qh);
        qh.setIp(hostConfig.getIp());
        List<HostConfigDto>  hostConfigDtos=hostConfigService.selectBySpecification(qh);
        if(null!=hostConfigDtos&&hostConfigDtos.size()>0){
            HostConfigDto old=hostConfigDtos.get(0);
            if(3==hostConfig.getMonitoringMethods()){
                return;
            }
            if(3!=old.getMonitoringMethods()){
                return;
            }
            if(StringUtil.isEmpty(old.getHostName())){
                hostConfig.setHostName(old.getHostName());
            }

        }
        hostConfigService.save(hostConfig);
    }


}
