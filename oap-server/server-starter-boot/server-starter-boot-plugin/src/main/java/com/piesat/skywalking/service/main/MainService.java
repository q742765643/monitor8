package com.piesat.skywalking.service.main;

import com.alibaba.fastjson.JSON;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.*;
import com.piesat.skywalking.mapper.HostConfigMapper;
import com.piesat.skywalking.vo.AlarmDistributionVo;
import com.piesat.skywalking.vo.MonitorViewVo;
import com.piesat.util.IndexNameUtil;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.NullUtil;
import com.piesat.util.StringUtil;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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
    private HostConfigMapper hostConfigMapper;
    //@Autowired
    //private AlarmEsLogService alarmEsLogService;
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;

    public List<MonitorViewVo> getMonitorViewVo() {
        List<MonitorViewVo> monitorViewVos = new ArrayList<>();

        HostConfigDto hostConfigDto = new HostConfigDto();
        NullUtil.changeToNull(hostConfigDto);
        hostConfigDto.setDeviceType(1);
        long link = hostConfigService.selectCount(hostConfigDto);
        MonitorViewVo linkView = new MonitorViewVo();
        linkView.setClassify("链路设备");
        linkView.setNum(link);
        monitorViewVos.add(linkView);

        NullUtil.changeToNull(hostConfigDto);
        hostConfigDto.setDeviceType(0);
        long host = hostConfigService.selectCount(hostConfigDto);
        MonitorViewVo hostView = new MonitorViewVo();
        hostView.setClassify("主机设备");
        hostView.setNum(host);
        monitorViewVos.add(hostView);

        FileMonitorDto fileMonitorDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        long files = fileMonitorService.selectCount(fileMonitorDto);
        MonitorViewVo fileView = new MonitorViewVo();
        fileView.setClassify("数据任务");
        fileView.setNum(files);
        monitorViewVos.add(fileView);

        ProcessConfigDto processConfigDto = new ProcessConfigDto();
        NullUtil.changeToNull(processConfigDto);
        long process = processConfigService.selectCount(processConfigDto);
        MonitorViewVo processView = new MonitorViewVo();
        processView.setClassify("进程任务");
        processView.setNum(process);
        monitorViewVos.add(processView);
        return monitorViewVos;
    }

    public List<Map<String, Object>>  getDeviceStatus(HostConfigDto hostConfigdto) {
     /*   List<Map<String, Object>> mapList=hostConfigMapper.findStateStatistics();
        Map<String, Object> map=new HashMap<>();
        if(null!=mapList && mapList.size()>0){
            for (int i=0;i<mapList.size();i++){
                map.putAll(mapList.get(i));
            }
        }
        String[] names=new String[]{"未知","一般","危险","故障","正常"};
        String[] values=new String[]{"11","0","1","2","3"};
        List<MonitorViewVo> monitorViewVos = new ArrayList<>();
        for(int i=0;i<names.length;i++){
            MonitorViewVo monitorViewVo=new MonitorViewVo();
            if(null!=map.get(values[i])){
                monitorViewVo.setNum(Long.parseLong(String.valueOf(map.get(values[i]))));
            }
            monitorViewVo.setClassify(names[i]);
            monitorViewVos.add(monitorViewVo);
        }*/
        Map<String, String> mapKey=new HashMap<>();
        mapKey.put("11","未知");
        mapKey.put("0","一般");
        mapKey.put("1","危险");
        mapKey.put("2","故障");
        mapKey.put("3","正常");
        List<HostConfigDto> hostConfigDtos=hostConfigService.selectBySpecification(hostConfigdto);
        List<Map<String, Object>> mapList=new ArrayList<>();
        if(null!=hostConfigDtos&&!hostConfigDtos.isEmpty()){
            for(int i=0;i< hostConfigDtos.size();i++){
                Map<String, Object> map=new HashMap<>();
                map.put("currentStatus",mapKey.get(String.valueOf(hostConfigDtos.get(i).getCurrentStatus())));
                map.put("ip",hostConfigDtos.get(i).getIp());
                mapList.add(map);
            }
        }
        return mapList;
    }

    public PageBean getAlarm(PageForm<AlarmLogDto> pageForm) {
        //return alarmEsLogService.selectPageList(pageForm);
        return null;
    }

    public List<AlarmDistributionVo> getAlarmDistribution(AlarmLogDto alarmLogDto) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endTime = format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -60*24);
        String beginTime = format.format(calendar.getTime());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        Map<String, Object> paramt = new HashMap<>();
        if (!StringUtil.isEmpty(alarmLogDto.getParams())) {
            paramt = JSON.parseObject(alarmLogDto.getParams(), Map.class);
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);
       /* if (StringUtils.isNotNullString((String) paramt.get("beginTime"))) {
            rangeQueryBuilder.gte((String) paramt.get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) paramt.get("endTime"))) {
            rangeQueryBuilder.lte((String) paramt.get("endTime"));
        }*/
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);

        ValueCountAggregationBuilder countByType = AggregationBuilders.count("count").field("device_type");
        TermsAggregationBuilder gourpByType = AggregationBuilders.terms("device_type").field("device_type")
                .subAggregation(countByType);
        searchSourceBuilder.aggregation(gourpByType);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(0);
        List<AlarmDistributionVo> list = new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM_LOG, searchSourceBuilder);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedLongTerms parsedLongTerms = aggregations.get("device_type");
            if (parsedLongTerms == null) {
                return null;
            }
            List<? extends Terms.Bucket> buckets = parsedLongTerms.getBuckets();
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    //AlarmDistributionVo alarmDistributionVo = new AlarmDistributionVo();
                    Terms.Bucket bucket = buckets.get(i);
                    String key =String.valueOf(bucket.getKey());
                    ParsedValueCount parsedValueCount = bucket.getAggregations().get("count");
                    Long count = parsedValueCount.getValue();
                    map.put(key,String.valueOf(count));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] titles=new String[]{"主机设备","链路设备","进程任务","数据任务"};
        for(int i=0;i<=3;i++){
            AlarmDistributionVo alarmDistributionVo = new AlarmDistributionVo();
            if(null!=map.get(String.valueOf(i))){
                alarmDistributionVo.setNum(Long.parseLong(map.get(String.valueOf(i))));
            }
            alarmDistributionVo.setClassify(titles[i]);
            list.add(alarmDistributionVo);
        }
        return list;

    }


    public List<FileStatisticsDto> getFileStatus() {
        List<FileStatisticsDto> list = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String indexName = IndexNameUtil.getIndexName(IndexNameConstant.T_MT_FILE_STATISTICS, new Date());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTime().getTime();
            long endTime = startTime + 86400 * 1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            long count = hits.getTotalHits().value;
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();
                FileStatisticsDto fileStatisticsDto = new FileStatisticsDto();
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
