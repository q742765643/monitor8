package com.piesat.skywalking.service.main;

import com.alibaba.fastjson.JSON;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.*;
import com.piesat.skywalking.vo.AlarmDistributionVo;
import com.piesat.skywalking.vo.MonitorViewVo;
import com.piesat.util.IndexNameUtil;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.StringUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import net.sf.saxon.trans.SymbolicName;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MainService {
    @Autowired
    private FileMonitorService fileMonitorService;
    @Autowired
    private HostConfigService hostConfigService;
    @Autowired
    private ProcessConfigService processConfigService;
    @Autowired
    private AlarmEsLogService alarmEsLogService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public List<MonitorViewVo> getMonitorViewVo(){
        List<MonitorViewVo> monitorViewVos=new ArrayList<>();

        HostConfigDto hostConfigDto=new HostConfigDto();
        hostConfigDto.setTriggerStatus(null);
        List<Integer> types=new ArrayList<>();
        types.add(2);
        types.add(3);
        types.add(4);
        hostConfigDto.setMediaTypes(types);
        long link=hostConfigService.selectCount(hostConfigDto);
        MonitorViewVo linkView=new MonitorViewVo();
        linkView.setClassify("链路设备");
        linkView.setNum(link);
        monitorViewVos.add(linkView);


        List<Integer> typeServer=new ArrayList<>();
        typeServer.add(0);
        typeServer.add(1);
        hostConfigDto.setMediaTypes(typeServer);
        long host=hostConfigService.selectCount(hostConfigDto);
        MonitorViewVo hostView=new MonitorViewVo();
        hostView.setClassify("主机设备");
        hostView.setNum(host);
        monitorViewVos.add(hostView);

        FileMonitorDto fileMonitorDto=new FileMonitorDto();
        fileMonitorDto.setTriggerStatus(null);
        long files=fileMonitorService.selectCount(fileMonitorDto);
        MonitorViewVo fileView=new MonitorViewVo();
        fileView.setClassify("数据任务");
        fileView.setNum(files);
        monitorViewVos.add(fileView);

        ProcessConfigDto processConfigDto=new ProcessConfigDto();
        long process=processConfigService.selectCount(processConfigDto);
        MonitorViewVo processView=new MonitorViewVo();
        processView.setClassify("进程任务");
        processView.setNum(process);
        monitorViewVos.add(processView);
        return monitorViewVos;
    }

    public List<HostConfigDto> getDeviceStatus(HostConfigDto hostConfigdto){
        return hostConfigService.selectBySpecification(hostConfigdto);
    }

    public PageBean getAlarm(PageForm<AlarmLogDto> pageForm){
        return alarmEsLogService.selectPageList(pageForm);
    }

    public  List<AlarmDistributionVo> getAlarmDistribution(AlarmLogDto alarmLogDto){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        Map<String, Object> paramt = new HashMap<>();
        if (!StringUtil.isEmpty(alarmLogDto.getParams())) {
            paramt = JSON.parseObject(alarmLogDto.getParams(), Map.class);
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        if (StringUtils.isNotNullString((String) paramt.get("beginTime"))) {
            rangeQueryBuilder.gte((String) paramt.get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) paramt.get("endTime"))) {
            rangeQueryBuilder.lte((String) paramt.get("endTime"));
        }
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);

        ValueCountAggregationBuilder countByType = AggregationBuilders.count("count").field("device_type");
        TermsAggregationBuilder gourpByType = AggregationBuilders.terms("device_type").field("device_type")
                .subAggregation(countByType);
        searchSourceBuilder.aggregation(gourpByType);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(0);
        List<AlarmDistributionVo> list=new ArrayList<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM_LOG+"-*", searchSourceBuilder);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null){
                return null;
            }
            ParsedLongTerms parsedLongTerms=aggregations.get("device_type");
            if(parsedLongTerms==null){
                return null;
            }
            List<? extends Terms.Bucket> buckets = parsedLongTerms.getBuckets();
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    AlarmDistributionVo alarmDistributionVo=new AlarmDistributionVo();
                    Terms.Bucket bucket=buckets.get(i);
                    Long key= Long.parseLong(String.valueOf(bucket.getKey()));
                    ParsedValueCount parsedValueCount=bucket.getAggregations().get("count");
                    Long count=parsedValueCount.getValue();
                    if(key==0){
                        alarmDistributionVo.setClassify("主机设备");
                    }
                    if(key==1){
                        alarmDistributionVo.setClassify("链路设备");
                    }
                    if(key==2){
                        alarmDistributionVo.setClassify("进程任务");
                    }
                    if(key==3){
                        alarmDistributionVo.setClassify("数据任务");
                    }
                    alarmDistributionVo.setNum(count);
                    list.add(alarmDistributionVo);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;

    }


    public List<FileStatisticsDto> getFileStatus(){
        List<FileStatisticsDto> list=new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String indexName= IndexNameUtil.getIndexName(IndexNameConstant.T_MT_FILE_STATISTICS,new Date());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND,0);
            long startTime=calendar.getTime().getTime();
            long endTime=startTime+86400*1000;
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("start_time_s");
            rangeQueryBuilder.gte(format.format(startTime));
            rangeQueryBuilder.lte(format.format(endTime));
            rangeQueryBuilder.timeZone("+08:00");
            rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
            boolBuilder.filter(rangeQueryBuilder);
            searchSourceBuilder.size(1000);
            searchSourceBuilder.query(boolBuilder);
            SearchResponse response = elasticSearch7Client.search(indexName, searchSourceBuilder);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            long count=hits.getTotalHits().value;
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String,Object> source=hit.getSourceAsMap();
                FileStatisticsDto fileStatisticsDto=new FileStatisticsDto();
                fileStatisticsDto.setTaskId((String) source.get("task_id"));
                fileStatisticsDto.setFilenameRegular((String) source.get("filename_regular"));
                fileStatisticsDto.setFileNum(Long.parseLong(String.valueOf(source.get("file_num"))));
                fileStatisticsDto.setFileSize(Long.parseLong(String.valueOf(source.get("file_size"))));
                fileStatisticsDto.setStartTimeL(Long.parseLong(String.valueOf(source.get("start_time_l"))));
                fileStatisticsDto.setStartTimeS(JsonParseUtil.formateToDate(String.valueOf(source.get("start_time_s"))));
                fileStatisticsDto.setStatus(Integer.parseInt(String.valueOf(source.get("status"))));
                fileStatisticsDto.setRealFileNum(Long.parseLong(String.valueOf(source.get("real_file_num"))));
                fileStatisticsDto.setRealFileSize(Long.parseLong(String.valueOf(source.get("real_file_size"))));
                fileStatisticsDto.setPerFileNum(Float.parseFloat(String.valueOf(source.get("per_file_num"))));
                fileStatisticsDto.setPerFileSize(Float.parseFloat(String.valueOf(source.get("per_file_size"))));
                list.add(fileStatisticsDto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;

    }

}
