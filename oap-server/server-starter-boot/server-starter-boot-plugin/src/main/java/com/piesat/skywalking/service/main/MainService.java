package com.piesat.skywalking.service.main;

import com.alibaba.fastjson.JSON;
import com.piesat.common.utils.StringUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.alarm.AlarmEsLogService;
import com.piesat.skywalking.api.alarm.AlarmUnService;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.api.host.HostConfigService;
import com.piesat.skywalking.api.host.ProcessConfigService;
import com.piesat.skywalking.dto.*;
import com.piesat.skywalking.mapper.HostConfigMapper;
import com.piesat.skywalking.service.timing.CronExpression;
import com.piesat.skywalking.vo.AlarmDistributionVo;
import com.piesat.skywalking.vo.MonitorViewVo;
import com.piesat.util.*;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
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
    @Autowired
    private AlarmUnService alarmUnService;

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

    public List<AlarmLogDto> getAlarm(AlarmLogDto query) {
        query.setStatus(0);
        List<AlarmLogDto> alarmLogDtos=alarmUnService.selectList(query);
        //return alarmEsLogService.selectPageList(pageForm);
        return alarmLogDtos;
    }

    public List<AlarmDistributionVo> getAlarmDistribution(AlarmLogDto alarmLogDto) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  /*      Date date = new Date();
        String endTime = format.format(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -60*24);
        String beginTime = format.format(calendar.getTime());*/
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder status = QueryBuilders.matchQuery("status", 0);
        boolBuilder.must(status);
        Map<String, Object> paramt = new HashMap<>();
         /*if (!StringUtil.isEmpty(alarmLogDto.getParams())) {
            paramt = JSON.parseObject(alarmLogDto.getParams(), Map.class);
        }
       RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
        rangeQueryBuilder.gte(beginTime);
        rangeQueryBuilder.lte(endTime);*/
       /* if (StringUtils.isNotNullString((String) paramt.get("beginTime"))) {
            rangeQueryBuilder.gte((String) paramt.get("beginTime"));
        }
        if (StringUtils.isNotNullString((String) paramt.get("endTime"))) {
            rangeQueryBuilder.lte((String) paramt.get("endTime"));
        }*/
       /* rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);*/

        ValueCountAggregationBuilder countByType = AggregationBuilders.count("count").field("device_type");
        TermsAggregationBuilder gourpByType = AggregationBuilders.terms("device_type").field("device_type")
                .subAggregation(countByType);
        searchSourceBuilder.aggregation(gourpByType);
        searchSourceBuilder.query(boolBuilder);
        searchSourceBuilder.size(0);
        List<AlarmDistributionVo> list = new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_ALARM, searchSourceBuilder);
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
    public Map<String,Object> getFileStatus() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        long endTime = calendar.getTime().getTime()+3600*1000;
        long startTime = endTime - 86400 * 1000;

        List<String> hoursList=new ArrayList<>();
        List<String> hoursList1=new ArrayList<>();
        Map<String,String> hoursMap=new LinkedHashMap<>();
        SimpleDateFormat qh=new SimpleDateFormat("d/H");
        SimpleDateFormat qh1=new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Calendar calendarTemp = Calendar.getInstance();
        calendarTemp.setTimeInMillis(startTime);
        for(int i=0;i<30;i++){
            if(calendarTemp.getTime().getTime()>endTime){
                break;
            }
            int hour=calendarTemp.get(Calendar.HOUR_OF_DAY);
            if(i==0||hour==0){
                hoursList.add(qh.format(calendarTemp.getTimeInMillis()));
            }else {
                hoursList.add(String.valueOf(hour));
            }
            hoursList1.add(qh1.format(calendarTemp.getTimeInMillis()));
            hoursMap.put(qh.format(calendarTemp.getTimeInMillis()),String.valueOf(i));
            calendarTemp.setTimeInMillis(calendarTemp.getTimeInMillis()+3600*1000);
        }

        FileMonitorDto fileMonitorDto=new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        fileMonitorDto.setTriggerStatus(1);
        List<FileMonitorDto> fileMonitorList=fileMonitorService.selectBySpecification(fileMonitorDto);
        List<String> daysList=new ArrayList<>();
        Map<String,String> daysMap=new HashMap<>();
        if(null!=fileMonitorList){
            for(int i=0;i<fileMonitorList.size();i++){
                daysList.add(fileMonitorList.get(i).getTaskName());
                daysMap.put(fileMonitorList.get(i).getId(),String.valueOf(i));
            }
        }


        List<Integer[]> data = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String indexName = IndexNameConstant.T_MT_FILE_STATISTICS;
        Map<String,Map<String,Long>> has=new LinkedHashMap<>();
        try {
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("d_data_time");
            rangeQueryBuilder.gte(format.format(startTime));
            rangeQueryBuilder.lte(format.format(endTime));
            rangeQueryBuilder.timeZone("+08:00");
            rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
            boolBuilder.filter(rangeQueryBuilder);
            searchSourceBuilder.size(10000);
            searchSourceBuilder.query(boolBuilder);
            searchSourceBuilder.sort("d_data_time",SortOrder.ASC);
            SearchResponse response = elasticSearch7Client.search(indexName, searchSourceBuilder);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();
                String taskId= (String) source.get("task_id");
                long startTimel= 0;
                try {
                    startTimel = JsonParseUtil.formateToDate(String.valueOf(source.get("d_data_time"))).getTime();
                } catch (Exception e) {
                    continue;
                }
                long file_num=new BigDecimal(String.valueOf(source.get("file_num"))).longValue();
                long real_file_num=new BigDecimal(String.valueOf(source.get("real_file_num"))).longValue();
                long late_num=new BigDecimal(String.valueOf(source.get("late_num"))).longValue();
                String hour=qh.format(startTimel);
                if(StringUtil.isNotEmpty(daysMap.get(taskId))&&StringUtil.isNotEmpty(hoursMap.get(hour))){
                    Integer[] d=new Integer[3];
                    try {
                        d[0]=Integer.parseInt(hoursMap.get(hour));
                        d[1]=Integer.parseInt(daysMap.get(taskId));
                        String key=d[0]+"#"+d[1];
                        if(has.containsKey(key)){
                            has.get(key).put("file_num",has.get(key).get("file_num")+file_num);
                            has.get(key).put("real_file_num",has.get(key).get("real_file_num")+real_file_num);
                            has.get(key).put("late_num",has.get(key).get("late_num")+late_num);
                        }else{
                            Map<String,Long> hh=new HashMap<>();
                            hh.put("file_num",file_num);
                            hh.put("real_file_num",real_file_num);
                            hh.put("late_num",late_num);
                            has.put(key,hh);
                        }


                    } catch (Exception e) {
                        System.out.println("任务id"+taskId);
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String[]> tips=new ArrayList<>();
        for (Map.Entry<String, Map<String,Long>> entry : has.entrySet()) {
            Integer[] d = new Integer[3];
            d[0] = Integer.parseInt(entry.getKey().split("#")[0]);
            d[1] = Integer.parseInt(entry.getKey().split("#")[1]);
            Map<String, Long> hh = entry.getValue();
            long file_num = hh.get("file_num");
            long real_file_num = hh.get("real_file_num");
            long late_num = hh.get("late_num");
            Integer onTimeRate = new BigDecimal(real_file_num * 100).divide(new BigDecimal(file_num), 2, BigDecimal.ROUND_UP).intValue();
            Integer rate = new BigDecimal((real_file_num +late_num)* 100).divide(new BigDecimal(file_num), 2, BigDecimal.ROUND_UP).intValue();
            d[2] = rate;
            if (file_num > 0) {
                if(late_num>0){
                    System.out.println(11);
                }
                data.add(d);
                String[] tip = new String[]{String.valueOf(file_num), String.valueOf(real_file_num), String.valueOf(late_num),onTimeRate+"%", hoursList1.get(d[0])};
                tips.add(tip);
            }
        }
        Map<String,Object> returnMap=new HashMap<>();
        returnMap.put("days",daysList);
        returnMap.put("hours",hoursList);
        returnMap.put("data",data);
        returnMap.put("tip",tips);
        return returnMap;

    }


/*
    public Map<String,Object> getFileStatus() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //calendar.set(Calendar.MINUTE, 0);
        //calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.MILLISECOND, 0);
        long endTime = calendar.getTime().getTime()+3600*1000;
        long startTime = endTime - 86400 * 1000;

        List<String> hoursList=new ArrayList<>();
        Map<String,String> hoursMap=new LinkedHashMap<>();
        SimpleDateFormat qh=new SimpleDateFormat("d/H");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Calendar calendarTemp = Calendar.getInstance();
        calendarTemp.setTimeInMillis(startTime);
        for(int i=0;i<30;i++){
             if(calendarTemp.getTime().getTime()>endTime){
                break;
             }
             int hour=calendarTemp.get(Calendar.HOUR_OF_DAY);
             if(i==0||hour==0){
                 hoursList.add(qh.format(calendarTemp.getTimeInMillis()));
             }else {
                 hoursList.add(String.valueOf(hour));
             }
             hoursMap.put(qh.format(calendarTemp.getTimeInMillis()),String.valueOf(i));
             calendarTemp.setTimeInMillis(calendarTemp.getTimeInMillis()+3600*1000);
        }

        FileMonitorDto fileMonitorDto=new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        List<FileMonitorDto> fileMonitorList=fileMonitorService.selectBySpecification(fileMonitorDto);
        List<String> daysList=new ArrayList<>();
        Map<String,String> daysMap=new HashMap<>();
        if(null!=fileMonitorList){
            for(int i=0;i<fileMonitorList.size();i++){
                daysList.add(fileMonitorList.get(i).getTaskName());
                daysMap.put(fileMonitorList.get(i).getId(),String.valueOf(i));
            }
        }


        List<Integer[]> data = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String indexName = IndexNameConstant.T_MT_FILE_STATISTICS;
        Map<String,Integer> has=new HashMap<>();
        try {
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("d_data_time");
            rangeQueryBuilder.gte(format.format(startTime));
            rangeQueryBuilder.lte(format.format(endTime));
            rangeQueryBuilder.timeZone("+08:00");
            rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
            boolBuilder.filter(rangeQueryBuilder);
            searchSourceBuilder.size(10000);
            searchSourceBuilder.query(boolBuilder);
            searchSourceBuilder.sort("d_data_time",SortOrder.ASC);
            SearchResponse response = elasticSearch7Client.search(indexName, searchSourceBuilder);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();
                String taskId= (String) source.get("task_id");
                long startTimel= 0;
                try {
                    startTimel = JsonParseUtil.formateToDate(String.valueOf(source.get("d_data_time"))).getTime();
                } catch (Exception e) {
                    continue;
                }
                String status=String.valueOf(source.get("status"));
                String hour=qh.format(startTimel);
                if(StringUtil.isNotEmpty(daysMap.get(taskId))&&StringUtil.isNotEmpty(hoursMap.get(hour))){
                    Integer[] d=new Integer[3];
                    try {
                        d[0]=Integer.parseInt(hoursMap.get(hour));
                        d[1]=Integer.parseInt(daysMap.get(taskId));
                        if(StringUtil.isEmpty(status)||"null".equals(status)){
                            status="4";
                        }
                        d[2]=Integer.parseInt(status);
                        if(null==has.get(d[0]+"#"+d[1])){
                            has.put(d[0]+"#"+d[1],d[2]);
                        }else{
                            Integer d1=has.get(d[0]+"#"+d[1]);
                            if((d1<3||d1==4)&&d[2]<3&&d[2]>d1){
                                has.put(d[0]+"#"+d[1],d[2]);
                            }
                            if(d1==3&&d[2]!=3){
                                has.put(d[0]+"#"+d[1],d[2]);
                            }

                        }
                    } catch (NumberFormatException e) {
                      System.out.println("任务id"+taskId);
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Integer> entry : has.entrySet()) {
            Integer[] d=new Integer[3];
            d[0]=Integer.parseInt(entry.getKey().split("#")[0]);
            d[1]=Integer.parseInt(entry.getKey().split("#")[1]);
            d[2]=entry.getValue();
            data.add(d);
        }
        if(null!=fileMonitorList){
            for(int i=0;i<fileMonitorList.size();i++){
               FileMonitorDto fileMonitorDtoN=fileMonitorList.get(i);
               String taskId=fileMonitorDtoN.getId();
               if(fileMonitorDtoN.getTriggerStatus()==0&&StringUtil.isNotEmpty(fileMonitorDtoN.getJobCron())){
                   long nowTime=startTime;
                   if(nowTime<=fileMonitorDtoN.getUpdateTime().getTime()){
                       nowTime=fileMonitorDtoN.getUpdateTime().getTime();
                   }
                   while (nowTime <= endTime) {
                       try {
                           Date nextValidTime = new CronExpression(fileMonitorDtoN.getJobCron()).getNextValidTimeAfter(new Date(nowTime));
                           nowTime = nextValidTime.getTime();
                           if (nowTime <= endTime) {
                               String hour=qh.format(DdataTimeUtil.repalceRegx(fileMonitorDtoN.getFilenameRegular(),nowTime));
                               if(StringUtil.isNotEmpty(daysMap.get(taskId))&&StringUtil.isNotEmpty(hoursMap.get(hour))){
                                   Integer[] d=new Integer[3];
                                   d[0]=Integer.parseInt(hoursMap.get(hour));
                                   d[1]=Integer.parseInt(daysMap.get(taskId));
                                   d[2]=5;
                                   //if(!has.contains(d[0]+"#"+d[1])){
                                       data.add(d);
                                   //}
                               }
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               }
            }
        }
        Map<String,Object> returnMap=new HashMap<>();
        returnMap.put("days",daysList);
        returnMap.put("hours",hoursList);
        returnMap.put("data",data);
        return returnMap;

    }
*/

    public Map<String,Object> getProcess(){
        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(new Date());
        //calendar.set(Calendar.MINUTE, 0);
        //calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.MILLISECOND, 0);
        long endTime = calendar.getTime().getTime();
        long startTime = endTime - 86400 * 1000;

        List<String> hoursList=new ArrayList<>();
        List<String> longhoursList=new ArrayList<>();
        Map<String,String> hoursMap=new LinkedHashMap<>();
        SimpleDateFormat qh=new SimpleDateFormat("d/H");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Calendar calendarTemp = Calendar.getInstance();
        calendarTemp.setTimeInMillis(startTime);
        for(int i=0;i<30;i++){
            if(calendarTemp.getTime().getTime()>endTime){
                break;
            }
            int hour=calendarTemp.get(Calendar.HOUR_OF_DAY);
            if(i==0||hour==0){
                hoursList.add(qh.format(calendarTemp.getTimeInMillis()));
            }else {
                hoursList.add(String.valueOf(hour));
            }
            hoursMap.put(qh.format(calendarTemp.getTimeInMillis()),String.valueOf(i));
            longhoursList.add(format.format(calendarTemp.getTimeInMillis()));
            calendarTemp.setTimeInMillis(calendarTemp.getTimeInMillis()+3600*1000);

        }
        ProcessConfigDto pro=new ProcessConfigDto();
        NullUtil.changeToNull(pro);
        List<ProcessConfigDto> processConfigDtos= processConfigService.selectBySpecification(pro);
        List<String> daysList=new ArrayList<>();
        Map<String,String> daysMap=new HashMap<>();
        Map<Integer,List<Integer[]>> all=new HashMap<>();
        if(null!=processConfigDtos){
            for(int i=0;i<processConfigDtos.size();i++){
                daysList.add(processConfigDtos.get(i).getProcessName());
                daysMap.put(processConfigDtos.get(i).getId(),String.valueOf(i));
                List<Integer[]> list=new ArrayList<>();
                all.put(i,list);
                //numsMap.put(i,temp);
            }
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String indexName = IndexNameConstant.T_MT_PROCESS_DOWN_LOG;
        List<Integer[]> nums = new ArrayList<>();
        for(int i=0;i<processConfigDtos.size();i++){
            ProcessConfigDto processConfigDto=processConfigDtos.get(i);
            long nowTime=startTime;
            String id=processConfigDto.getId();
            if(nowTime<=processConfigDto.getCreateTime().getTime()){
                String hour1=qh.format(nowTime);
                String hour2=qh.format(processConfigDto.getCreateTime());
                if(StringUtil.isNotEmpty(daysMap.get(id))&&StringUtil.isNotEmpty(hoursMap.get(hour1))&&StringUtil.isNotEmpty(hoursMap.get(hour2))){
                    Integer[] d=new Integer[4];
                    d[0]=Integer.parseInt(daysMap.get(id));
                    d[1]=Integer.parseInt(hoursMap.get(hour1));
                    d[2]=Integer.parseInt(hoursMap.get(hour2));
                    if(d[1]==d[2]){
                        d[2]=d[1]+1;
                    }
                    d[3]=3;
                    nums.add(d);
                    all.get(d[0]).add(d);
                }
            }
        }
        try {
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            //boolBuilder.should(QueryBuilders.rangeQuery("start_time").from(startTime).includeLower(true));
            boolBuilder.should(QueryBuilders.rangeQuery("end_time").from(startTime).includeLower(true));
            searchSourceBuilder.size(1000);
            searchSourceBuilder.query(boolBuilder).sort("start_time", SortOrder.ASC);
            SearchResponse response = elasticSearch7Client.search(indexName, searchSourceBuilder);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();
                long stTime=new BigDecimal(String.valueOf(source.get("start_time"))).longValue();
                long edTime=new BigDecimal(String.valueOf(source.get("end_time"))).longValue();
                if(stTime<startTime){
                    stTime=startTime;
                }
                if(edTime>endTime){
                    edTime=endTime;
                }
                String id= (String) source.get("id");
                String type=String.valueOf(source.get("type"));
                String hour1=qh.format(stTime);
                String hour2=qh.format(edTime);
                if(StringUtil.isNotEmpty(daysMap.get(id))&&StringUtil.isNotEmpty(hoursMap.get(hour1))&&StringUtil.isNotEmpty(hoursMap.get(hour2))){
                    Integer[] d=new Integer[4];
                    d[0]=Integer.parseInt(daysMap.get(id));
                    d[1]=Integer.parseInt(hoursMap.get(hour1));
                    d[2]=Integer.parseInt(hoursMap.get(hour2));
                    if(d[1]==d[2]){
                        d[2]=d[1]+1;
                    }
                    d[3]=0;
                    if("1".equals(type)){
                        d[3]=1;
                    }
                    nums.add(d);
                    all.get(d[0]).add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i=0;i<processConfigDtos.size();i++){
            try {
                List<Integer[]> list=all.get(i);
                if(list.size()==0){
                    Integer[] d=new Integer[]{i,0,hoursList.size()-1,2};
                    nums.add(d);
                }
                int start=0;
                int end=0;
                for(int j=0;j<list.size();j++){
                    if(j==0){
                        start=0;
                        end=list.get(j)[1];
                        if(start!=end){
                            Integer[] d=new Integer[]{i,start,end,2};
                            nums.add(d);
                        }
                    }
                    if(j==list.size()-1){
                        start=list.get(j)[2];
                        end=hoursList.size()-1;
                        if(start!=end&&end>start){
                            Integer[] d=new Integer[]{i,start,end,2};
                            nums.add(d);
                        }
                    }
                    if(j>0){
                        start=list.get(j-1)[2];
                        end=list.get(j)[1];
                        if(start!=end){
                            Integer[] d=new Integer[]{i,start,end,2};
                            nums.add(d);
                        }
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String,Object> returnMap=new HashMap<>();
        returnMap.put("days",daysList);
        returnMap.put("hours",hoursList);
        returnMap.put("nums",nums);
        returnMap.put("longhoursList",longhoursList);
        return returnMap;
    }

}
