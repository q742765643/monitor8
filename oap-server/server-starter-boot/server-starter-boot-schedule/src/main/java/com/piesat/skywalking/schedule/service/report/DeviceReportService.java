package com.piesat.skywalking.schedule.service.report;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.util.IdUtils;
import com.piesat.util.IndexNameUtil;
import com.piesat.util.NullUtil;
import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7InsertRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.script.BucketAggregationScript;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.aggregations.pipeline.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : DeviceReportService
 * @Description : 日报
 * @Author : zzj
 * @Date: 2020-10-14 19:44
 */
@Service
public class DeviceReportService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @GrpcHthtClient
    private HostConfigService hostConfigService;

    public void getSystem(SystemQueryDto systemQueryDto){
        Map<String, Map<String,Object>> baseInfo=new HashMap<>();
        this.getCpu(systemQueryDto,baseInfo);
        this.getMemory(systemQueryDto,baseInfo);
        this.getFilesystem(systemQueryDto,baseInfo);
        this.getPacketloss(systemQueryDto,baseInfo);
        this.getProcess(systemQueryDto,baseInfo);
        this.getUptime(systemQueryDto,baseInfo);
        HostConfigDto hostConfigdto=new HostConfigDto();
        NullUtil.changeToNull(hostConfigdto);
        List<HostConfigDto> hostConfigDtos=hostConfigService.selectBySpecification(hostConfigdto);
        BulkRequest request = new BulkRequest();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time=new Date();
        for(HostConfigDto hostConfigDto:hostConfigDtos){
            Map<String,Object> source=this.getMap();
            source.put("ip",hostConfigDto.getIp());
            source.put("@timestamp",time);
            source.put("ishost",hostConfigDto.getIsHost());
            if(null!=baseInfo.get(hostConfigDto.getIp())){
                source.putAll(baseInfo.get(hostConfigDto.getIp()));
            }
            IndexRequest indexRequest = new ElasticSearch7InsertRequest(IndexNameConstant.T_MT_MEDIA_REPORT, hostConfigDto.getIp()+"_"+systemQueryDto.getStartTime()).source(source);
            request.add(indexRequest);
        }
        try {
            boolean flag=elasticSearch7Client.isExistsIndex(IndexNameConstant.T_MT_MEDIA_REPORT);
            if(!flag){
                Map<String, Object> ip = new HashMap<>();
                ip.put("type", "keyword");
                Map<String, Object> properties = new HashMap<>();
                properties.put("ip", ip);
                Map<String, Object> mapping = new HashMap<>();
                mapping.put("properties", properties);
                elasticSearch7Client.createIndex(IndexNameConstant.T_MT_MEDIA_REPORT,new HashMap<>(),mapping);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        elasticSearch7Client.synchronousBulk(request);

    }

    public void getCpu(SystemQueryDto systemQueryDto,Map<String, Map<String,Object>> baseInfo){
        SearchSourceBuilder search=this.buildWhere(systemQueryDto,"cpu");
        AvgAggregationBuilder avgPct = AggregationBuilders.avg("avg_cpu_pct").field("system.cpu.total.norm.pct").format("0.0000");
        MaxAggregationBuilder maxPct = AggregationBuilders.max("max_cpu_pct").field("system.cpu.total.norm.pct").format("0.0000");
        TermsAggregationBuilder groupById=AggregationBuilders.terms("groupby_ip").field("host.name").size(10000);
        groupById.subAggregation(avgPct);
        groupById.subAggregation(maxPct);
        search.aggregation(groupById);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null){
                return;
            }
            ParsedStringTerms terms=aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for(int i=0;i<buckets.size();i++){
                Terms.Bucket bucket=buckets.get(i);
                String ip=bucket.getKeyAsString();
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg_cpu_pct");
                ParsedMax parsedMax = bucket.getAggregations().get("max_cpu_pct");
                float avgCpuPct=Float.parseFloat(parsedAvg.getValueAsString());
                float maxCpuPct=Float.parseFloat(parsedMax.getValueAsString());
                Map<String,Object> value=this.getValueMap(baseInfo,ip);
                value.put("avg.cpu.pct",avgCpuPct);
                value.put("max.cpu.pct",maxCpuPct);
                baseInfo.put(ip,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void getMemory(SystemQueryDto systemQueryDto,Map<String, Map<String,Object>> baseInfo){
        SearchSourceBuilder search=this.buildWhere(systemQueryDto,"memory");
        AvgAggregationBuilder avgPct = AggregationBuilders.avg("avg_memory_pct").field("system.memory.used.pct").format("0.0000");
        MaxAggregationBuilder maxPct = AggregationBuilders.max("max_memory_pct").field("system.memory.used.pct").format("0.0000");
        TermsAggregationBuilder groupById=AggregationBuilders.terms("groupby_ip").field("host.name").size(10000);
        groupById.subAggregation(avgPct);
        groupById.subAggregation(maxPct);
        search.aggregation(groupById);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null){
                return;
            }
            ParsedStringTerms terms=aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for(int i=0;i<buckets.size();i++){
                Terms.Bucket bucket=buckets.get(i);
                String ip=bucket.getKeyAsString();
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg_memory_pct");
                ParsedMax parsedMax = bucket.getAggregations().get("max_memory_pct");
                float avgMemoryPct=Float.parseFloat(parsedAvg.getValueAsString());
                float maxMemoryPct=Float.parseFloat(parsedMax.getValueAsString());
                Map<String,Object> value=this.getValueMap(baseInfo,ip);
                value.put("avg.memory.pct",avgMemoryPct);
                value.put("max.memory.pct",maxMemoryPct);
                baseInfo.put(ip,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getFilesystem(SystemQueryDto systemQueryDto,Map<String, Map<String,Object>> baseInfo){
        SearchSourceBuilder search=this.buildWhere(systemQueryDto,"filesystem");
        TermsAggregationBuilder groupbyIp= AggregationBuilders.terms("groupby_ip ").field("host.name").size(10000);
        TermsAggregationBuilder groupbyTime= AggregationBuilders.terms("groupby_time").field("@timestamp").size(1);
        SumAggregationBuilder sumFilesystemUse = AggregationBuilders.sum("sum_filesystem_use").field("system.filesystem.used.bytes").format("0.0000");
        SumAggregationBuilder sumFilesystem = AggregationBuilders.sum("sum_filesystem").field("system.filesystem.total").format("0.0000");
        Script select = new Script("params.sum_filesystem_use/params.sum_filesystem");
        Map<String, String> bucketsPath = new HashMap<>();
        bucketsPath.put("sum_filesystem", "sum_filesystem");
        bucketsPath.put("sum_filesystem_use","sum_filesystem_use");
        BucketScriptPipelineAggregationBuilder bucketScript = PipelineAggregatorBuilders.bucketScript("avg_filesystem_pct", bucketsPath, select);
        bucketScript.format("0.0000");
        groupbyTime.subAggregation(sumFilesystemUse);
        groupbyTime.subAggregation(sumFilesystem);
        groupbyTime.subAggregation(bucketScript);
        groupbyIp.subAggregation(groupbyTime);
        MaxBucketPipelineAggregationBuilder maxFilesystemPct= new MaxBucketPipelineAggregationBuilder("max_filesystem_pct","groupby_time>avg_filesystem_pct").format("0.0000");
        groupbyIp.subAggregation(maxFilesystemPct);
        search.aggregation(groupbyIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null){
                return;
            }
            List<Aggregation> aggs= aggregations.asList();
            for(int i=0;i<aggs.size();i++){
               ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggs.get(i);
                List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
                for(int j=0;j<buckets.size();j++){
                    Terms.Bucket bucket=buckets.get(j);
                    String ip=bucket.getKeyAsString();
                    Map<String, Aggregation> map=bucket.getAggregations().getAsMap();
                    ParsedBucketMetricValue max= (ParsedBucketMetricValue) map.get("max_filesystem_pct");
                    Map<String,Object> value=this.getValueMap(baseInfo,ip);
                    value.put("max.filesystem.pct",Float.parseFloat(max.getValueAsString()));
                    baseInfo.put(ip,value);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getProcess(SystemQueryDto systemQueryDto,Map<String, Map<String,Object>> baseInfo){
        SearchSourceBuilder search=this.buildWhere(systemQueryDto,"process");
        TermsAggregationBuilder groupbyIp= AggregationBuilders.terms("groupby_ip ").field("host.name").size(10000);
        TermsAggregationBuilder groupbyTime= AggregationBuilders.terms("groupby_time").field("@timestamp").size(1);
        ValueCountAggregationBuilder sumProcess = AggregationBuilders.count("sum_process").field("process.pid");
        groupbyTime.subAggregation(sumProcess);
        groupbyIp.subAggregation(groupbyTime);
        MaxBucketPipelineAggregationBuilder maxProcessSize= new MaxBucketPipelineAggregationBuilder("max_process","groupby_time>sum_process");
        groupbyIp.subAggregation(maxProcessSize);
        search.aggregation(groupbyIp);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null){
                return;
            }
            List<Aggregation> aggs= aggregations.asList();
            for(int i=0;i<aggs.size();i++){
                ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggs.get(i);
                List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
                for(int j=0;j<buckets.size();j++){
                    Terms.Bucket bucket=buckets.get(j);
                    String ip=bucket.getKeyAsString();
                    Map<String, Aggregation> map=bucket.getAggregations().getAsMap();
                    ParsedBucketMetricValue max= (ParsedBucketMetricValue) map.get("max_process");
                    Map<String,Object> value=this.getValueMap(baseInfo,ip);
                    value.put("max.process.size",Float.parseFloat(max.getValueAsString()));
                    baseInfo.put(ip,value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto,String type){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchEvent = QueryBuilders.matchQuery("event.dataset", "system."+type);
        //MatchQueryBuilder matchIp = QueryBuilders.matchQuery("host.name", systemQueryDto.getIp());
        boolBuilder.must(matchEvent);
        //boolBuilder.must(matchIp);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        return searchSourceBuilder;
    }
    public void getPacketloss(SystemQueryDto systemQueryDto,Map<String, Map<String,Object>> baseInfo){
        SearchSourceBuilder search=this.buildWhere(systemQueryDto,"packet");
        AvgAggregationBuilder avgPct = AggregationBuilders.avg("avg_packet_pct").field("loss").format("0.0000");
        MaxAggregationBuilder maxPct = AggregationBuilders.max("max_packet_pct").field("loss").format("0.0000");
        TermsAggregationBuilder groupById=AggregationBuilders.terms("groupby_ip").field("host.name").size(10000);
        groupById.subAggregation(avgPct);
        groupById.subAggregation(maxPct);
        search.aggregation(groupById);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null){
                return;
            }
            ParsedStringTerms terms=aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for(int i=0;i<buckets.size();i++){
                Terms.Bucket bucket=buckets.get(i);
                String ip=bucket.getKeyAsString();
                ParsedAvg parsedAvg = bucket.getAggregations().get("avg_packet_pct");
                ParsedMax parsedMax = bucket.getAggregations().get("max_packet_pct");
                float avgPacketPct=Float.parseFloat(parsedAvg.getValueAsString());
                float maxPacketPct=Float.parseFloat(parsedMax.getValueAsString());
                Map<String,Object> value=this.getValueMap(baseInfo,ip);
                value.put("avg.packet.pct",avgPacketPct);
                value.put("max.packet.pct",maxPacketPct);
                baseInfo.put(ip,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getUptime (SystemQueryDto systemQueryDto,Map<String, Map<String,Object>> baseInfo){
        SearchSourceBuilder search=this.buildWhere(systemQueryDto,"uptime");
        MaxAggregationBuilder maxTime = AggregationBuilders.max("uptime").field("system.uptime.duration.ms").format("0.0000");
        TermsAggregationBuilder groupById=AggregationBuilders.terms("groupby_ip").field("host.name").size(10000);
        groupById.subAggregation(maxTime);
        search.aggregation(groupById);
        search.size(0);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.METRICBEAT+"-*", search);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null){
                return;
            }
            ParsedStringTerms terms=aggregations.get("groupby_ip");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            for(int i=0;i<buckets.size();i++){
                Terms.Bucket bucket=buckets.get(i);
                String ip=bucket.getKeyAsString();
                ParsedMax parsedMax = bucket.getAggregations().get("uptime");
                float maxUptime=Float.parseFloat(parsedMax.getValueAsString());
                Map<String,Object> value=this.getValueMap(baseInfo,ip);
                value.put("max.uptime.pct",maxUptime);
                baseInfo.put(ip,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Map<String,Object>  getValueMap(Map<String, Map<String,Object>> baseInfo,String ip){
            if(null!=baseInfo.get(ip)){
                return baseInfo.get(ip);
            }else {
                return new HashMap<>();
            }
    }

    public Map<String,Object> getMap(){
        Map<String,Object> map=new HashMap<>();
        map.put("avg.cpu.pct",0f);
        map.put("max.cpu.pct",0f);
        map.put("avg.memory.pct",0f);
        map.put("max.memory.pct",0f);
        map.put("max.filesystem.pct",0f);
        map.put("avg.packet.pct",0f);
        map.put("max.packet.pct",0f);
        map.put("max.uptime.pct",0f);
        map.put("max.process.size",0l);
        map.put("down.time",60*60*1000*60l);
        map.put("down.num",5l);
        map.put("alarm.num",10l);
        return map;
    }
}
