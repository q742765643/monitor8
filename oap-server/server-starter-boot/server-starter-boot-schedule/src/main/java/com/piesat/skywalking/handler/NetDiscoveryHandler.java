package com.piesat.skywalking.handler;

import com.alibaba.fastjson.JSON;
import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.discover.NetIpService;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.skywalking.dto.NetIpDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.skywalking.handler.base.BaseHandler;
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
@Service("netDiscoveryHandler")
public class NetDiscoveryHandler implements BaseHandler {
    @GrpcHthtClient
    private NetIpService netIpService;


    @Override
    public void execute(JobContext jobContext, ResultT<String> resultT) {
        NetDiscoveryDto netDiscoveryDto= (NetDiscoveryDto) jobContext.getHtJobInfoDto();
        List<String> ips = new ArrayList<>();
        try {
            if (netDiscoveryDto.getIpRange().indexOf("-") != -1) {
                ips = GetRangeIpUtil.GetIpListWithMask(netDiscoveryDto.getIpRange());
            } else if(netDiscoveryDto.getIpRange().indexOf(",") != -1){
                String[] ipStr=netDiscoveryDto.getIpRange().split(",");
                ips=Arrays.asList(ipStr);
                //ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange(), 24);
            } else if(netDiscoveryDto.getIpRange().indexOf("，") != -1){
                String[] ipStr=netDiscoveryDto.getIpRange().split("，");
                ips=Arrays.asList(ipStr);
                //ips = GetRangeIpUtil.GetIpListWithMask(autoDiscoveryDto.getIpRange(), 24);
            }
            if (ips.size()>0){
                netIpService.deleteByWhere(netDiscoveryDto.getId());
                List<String> useIp = Ping.GetPingSuccess(ips);
                if(null==useIp){
                    return;
                }
                if(useIp.size()==0){
                    return;
                }

                for(String ip:useIp){
                    NetIpDto netIpDto=new NetIpDto();
                    netIpDto.setIp(ip);
                    netIpDto.setDiscoveryId(netDiscoveryDto.getId());
                    netIpDto.setCurrentStatus(1);
                    netIpService.save(netIpDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
