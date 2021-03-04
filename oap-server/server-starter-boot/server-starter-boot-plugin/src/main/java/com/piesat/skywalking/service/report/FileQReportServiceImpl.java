package com.piesat.skywalking.service.report;

import com.piesat.common.utils.CellModel;
import com.piesat.common.utils.ExcelToPdf;
import com.piesat.common.utils.ServletUtils;
import com.piesat.constant.IndexNameConstant;
import com.piesat.skywalking.api.folder.FileMonitorService;
import com.piesat.skywalking.api.folder.FileQReportService;
import com.piesat.skywalking.dto.FileMonitorDto;
import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.vo.ImageVo;
import com.piesat.util.ImageUtils;
import com.piesat.util.JsonParseUtil;
import com.piesat.util.NullUtil;
import com.piesat.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.skywalking.oap.server.storage.plugin.elasticsearch7.client.ElasticSearch7Client;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @ClassName : FileQReportServiceImpl
 * @Description : 文件监控报表
 * @Author : zzj
 * @Date: 2020-10-28 14:15
 */
@Service
public class FileQReportServiceImpl implements FileQReportService {
    @Autowired
    private ElasticSearch7Client elasticSearch7Client;
    @Autowired
    private FileMonitorService fileMonitorService;

    public List<Map<String, String>> findHeader() {
        List<Map<String, String>> list = new ArrayList<>();
        FileMonitorDto fileMonitorDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        List<FileMonitorDto> fileMonitorDtos = fileMonitorService.selectBySpecification(fileMonitorDto);
        for (int i = 0; i < fileMonitorDtos.size(); i++) {
            FileMonitorDto file = fileMonitorDtos.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("taskId", file.getId());
            map.put("title", file.getTaskName());
            list.add(map);
        }
        return list;
    }

    public List<Map<String, Object>> fileLineDiagram(String taskId) {
        List<Map<String, Object>> list = new ArrayList<>();
        FileMonitorDto fileMonitorDto=fileMonitorService.findById(taskId);
        long endTime=System.currentTimeMillis();
        /*if(1==fileMonitorDto.getIsUt()){
            endTime=endTime-3600*8*1000;
        }*/
        long startTime=endTime-86400*1000;
        SystemQueryDto systemQueryDto=new SystemQueryDto();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        systemQueryDto.setStartTime(format.format(startTime));
        systemQueryDto.setEndTime(format.format(endTime));
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("d_data_time");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        search.query(boolBuilder);
        MatchQueryBuilder matchTaskId = QueryBuilders.matchQuery("task_id", taskId);
        boolBuilder.must(matchTaskId);
        search.query(boolBuilder).sort("d_data_time", SortOrder.DESC);
        search.size(10000);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map jsonMap = new LinkedHashMap();
                JsonParseUtil.parseJSON2Map(jsonMap, hit.getSourceAsString(), null);
                jsonMap.put("d_data_time",JsonParseUtil.formateToDate((String) jsonMap.get("d_data_time")));
                list.add(jsonMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.reverse(list);
        return list;
    }
    public Map<String, Object> findFileReportLineChart(SystemQueryDto systemQueryDto){
        Map<String, String> mapTaskName=this.findHeaderRow();
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("d_data_time");
        dateHis.dateHistogramInterval(DateHistogramInterval.days(1));
        dateHis.format("yyyy-MM-dd HH:mm:ss");
        dateHis.timeZone(ZoneId.of("Asia/Shanghai"));
        List<Map<String, String>> list = new ArrayList<>();
        List<String> taskNameList=new ArrayList<>();
        List<String> taskIdList=new ArrayList<>();
        TreeSet<String> timeSet=new TreeSet<>();
        TermsAggregationBuilder groupByTaskId = AggregationBuilders.terms("groupByTaskId").field("task_id").size(10000);
        SumAggregationBuilder sumRealFileSize = AggregationBuilders.sum("sumRealFileSize").field("real_file_size").format("0.0000");
        SumAggregationBuilder sumRealFileNum = AggregationBuilders.sum("sumRealFileNum").field("real_file_num").format("0.0000");
        SumAggregationBuilder sumLateNum = AggregationBuilders.sum("sumLateNum").field("late_num").format("0.0000");
        SumAggregationBuilder sumFileNum = AggregationBuilders.sum("sumFileNum").field("file_num").format("0.0000");
        dateHis.subAggregation(sumRealFileNum);
        dateHis.subAggregation(sumRealFileSize);
        dateHis.subAggregation(sumLateNum);
        dateHis.subAggregation(sumFileNum);
        groupByTaskId.subAggregation(dateHis);
        search.aggregation(groupByTaskId);
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedStringTerms parsedStringTerms = aggregations.get("groupByTaskId");
            List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("Asia/Shanghai"));
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    Terms.Bucket bucket = buckets.get(i);
                    Map<String, Aggregation> agg = bucket.getAggregations().asMap();
                    ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) agg.get("@timestamp");
                    List<? extends Histogram.Bucket> bucketSum = parsedDateHistogram.getBuckets();
                    Map<String, String> map = new HashMap<>();
                    if (StringUtil.isEmpty( mapTaskName.get(bucket.getKeyAsString()))){
                        continue;
                    }
                    taskIdList.add(bucket.getKeyAsString());
                    taskNameList.add(mapTaskName.get(bucket.getKeyAsString()));
                    for (int j = 0; j < bucketSum.size(); j++) {
                        Histogram.Bucket bucketV = bucketSum.get(j);
                        ZonedDateTime date = (ZonedDateTime) bucketV.getKey();
                        ParsedSum sumRealFileNumV = bucketV.getAggregations().get("sumRealFileNum");
                        ParsedSum sumLateNumV = bucketV.getAggregations().get("sumLateNum");
                        ParsedSum sumFileNumV = bucketV.getAggregations().get("sumFileNum");
                        long sumRealFileNumL = new BigDecimal(sumRealFileNumV.getValueAsString()).longValue();
                        long sumLateNumL = new BigDecimal(sumLateNumV.getValueAsString()).longValue();
                        long sumFileNumL = new BigDecimal(sumFileNumV.getValueAsString()).longValue();
                        if (sumFileNumL > 0) {
                            float toQuoteRate = new BigDecimal(sumRealFileNumL + sumLateNumL).divide(new BigDecimal(sumFileNumL), 4, BigDecimal.ROUND_HALF_UP).floatValue();
                            map.put(formatter.format(date), String.valueOf(toQuoteRate*100));
                        }else {
                            map.put(formatter.format(date), "100");
                        }
                        list.add(map);
                        timeSet.add(formatter.format(date));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String,Object>> dataList=new ArrayList<>();
        for(int i=0;i<taskIdList.size();i++){
            Map<String,Object> map=new HashMap<>();
            List<Float> data=new ArrayList<>();
            Map<String,String> time=list.get(i);
            Iterator iter = timeSet.iterator();
            while (iter.hasNext()) {
                String key= (String) iter.next();
                if(StringUtil.isNotEmpty(time.get(key))){
                    data.add(new BigDecimal(time.get(key)).floatValue());
                }else {
                    data.add(0f);
                }
            }
            map.put("name",taskNameList.get(i));
            map.put("data",data);
            dataList.add(map);
        }
        Map<String, Object> result=new HashMap<>();
        result.put("title",taskNameList);
        result.put("time",timeSet);
        result.put("data",dataList);
        return result;
    }

    public Map<String, Object> findFileReport(SystemQueryDto systemQueryDto,List<String> taskLists) {
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("d_data_time");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        if(taskLists.size()==0){
            boolBuilder.must(QueryBuilders.termQuery("task_id", "null"));
        }else {
            boolBuilder.must(QueryBuilders.termsQuery("task_id", taskLists));
        }
        search.query(boolBuilder);
        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("d_data_time");
        dateHis.dateHistogramInterval(DateHistogramInterval.days(1));
        dateHis.format("yyyy-MM-dd HH:mm:ss");
        dateHis.timeZone(ZoneId.of("Asia/Shanghai"));
        TermsAggregationBuilder groupByTaskId = AggregationBuilders.terms("groupByTaskId").field("task_id").size(10000);
        SumAggregationBuilder sumRealFileSize = AggregationBuilders.sum("sumRealFileSize").field("real_file_size").format("0.0000");
        SumAggregationBuilder sumRealFileNum = AggregationBuilders.sum("sumRealFileNum").field("real_file_num").format("0.0000");
        SumAggregationBuilder sumLateNum = AggregationBuilders.sum("sumLateNum").field("late_num").format("0.0000");
        SumAggregationBuilder sumFileNum = AggregationBuilders.sum("sumFileNum").field("file_num").format("0.0000");
        groupByTaskId.subAggregation(sumRealFileNum);
        groupByTaskId.subAggregation(sumRealFileSize);
        groupByTaskId.subAggregation(sumLateNum);
        groupByTaskId.subAggregation(sumFileNum);
        dateHis.subAggregation(groupByTaskId);
        search.aggregation(dateHis);
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String,Object>> mergeCells=new ArrayList<>();
        StringBuilder stringBuilder=new StringBuilder();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedDateHistogram parsedDateHistogram = aggregations.get("@timestamp");
            List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    Histogram.Bucket bucket = buckets.get(i);
                    ZonedDateTime date = (ZonedDateTime) bucket.getKey();
                    //long time = Timestamp.from(date.toInstant()).getTime();
                    Map<String, Aggregation> agg = bucket.getAggregations().asMap();
                    ParsedStringTerms parsedStringTerms = (ParsedStringTerms) agg.get("groupByTaskId");
                    List<? extends Terms.Bucket> bucketSum = parsedStringTerms.getBuckets();
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamp", formatter.format(date));
                    for (int j = 0; j < bucketSum.size(); j++) {
                        Terms.Bucket bucketV = bucketSum.get(j);
                        String taskId = bucketV.getKeyAsString();
                        ParsedSum sumRealFileNumV = bucketV.getAggregations().get("sumRealFileNum");
                        ParsedSum sumLateNumV = bucketV.getAggregations().get("sumLateNum");
                        ParsedSum sumRealFileSizeV = bucketV.getAggregations().get("sumRealFileSize");
                        ParsedSum sumFileNumV = bucketV.getAggregations().get("sumFileNum");
                        long sumRealFileNumL = new BigDecimal(sumRealFileNumV.getValueAsString()).longValue();
                        long sumLateNumL = new BigDecimal(sumLateNumV.getValueAsString()).longValue();
                        long sumFileNumL = new BigDecimal(sumFileNumV.getValueAsString()).longValue();
                        if (sumFileNumL > 0) {
                            float toQuoteRate = new BigDecimal((sumRealFileNumL + sumLateNumL)*100).divide(new BigDecimal(sumFileNumL), 0, BigDecimal.ROUND_HALF_UP).floatValue();
                            map.put(taskId, toQuoteRate);
                        }else {
                            map.put(taskId, 100);
                        }
                    }
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileMonitorDto fileMonitorDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        List<FileMonitorDto> fileMonitorDtos = fileMonitorService.selectBySpecification(fileMonitorDto);
        Map<String,String> taskNameMap=new HashMap<>();
        for (int i = 0; i < fileMonitorDtos.size(); i++) {
            FileMonitorDto file = fileMonitorDtos.get(i);
            taskNameMap.put(file.getId(),file.getTaskName());
        }
        Map<String,Object> mergeCell=new HashMap<>();
        mergeCell.put("row",list.size()+1);
        mergeCell.put("rowspan",1);
        mergeCell.put("col",1);
        mergeCell.put("colspan",taskLists.size()+1);
        mergeCells.add(mergeCell);
        Map<String,Float> mapHjRT=new HashMap<>();
        int fate=0;
        for(int i=0;i<list.size();i++){
            Map<String, Object> map = list.get(i);
            float toQuoteRateSum=0;
            for(int j=0;j<taskLists.size();j++){
                float toQuoteRate=0;
                if(null!=map.get(taskLists.get(j))){
                    toQuoteRate = (float) map.get(taskLists.get(j));
                }else{
                    map.put(taskLists.get(j),0);
                }
                toQuoteRateSum=toQuoteRateSum+toQuoteRate;
                if(0==i){
                    mapHjRT.put(taskLists.get(j),toQuoteRate);
                }else {
                    mapHjRT.put(taskLists.get(j),mapHjRT.get(taskLists.get(j))+toQuoteRate);
                }
            }
            BigDecimal toQuoteRateC=new BigDecimal(toQuoteRateSum).divide(new BigDecimal(taskLists.size()),0,BigDecimal.ROUND_HALF_UP);
            map.put("hjC",toQuoteRateC);
            if(toQuoteRateC.compareTo(new BigDecimal(100))>-1){
                fate++;
            }

        }
        Map<String,Object> mapHjR=new HashMap<>();
        BigDecimal hjoQuoteRateT=new BigDecimal(0);
        StringBuilder zlBl=new StringBuilder();
        for (Map.Entry<String,Float> entry : mapHjRT.entrySet()) {
            BigDecimal oQuoteRate=new BigDecimal(0);
            if(list.size()>0){
                 oQuoteRate=new BigDecimal(entry.getValue()).divide(new BigDecimal(list.size()),0,BigDecimal.ROUND_HALF_UP);
            }
            hjoQuoteRateT=hjoQuoteRateT.add(oQuoteRate);
            mapHjR.put(entry.getKey(),oQuoteRate);
            if(oQuoteRate.compareTo(new BigDecimal(100))>-1){
                zlBl.append(taskNameMap.get(entry.getKey())+",");
            }
        }
        BigDecimal hjoQuoteRateC=new BigDecimal(0);
        if(taskLists.size()>0){
             hjoQuoteRateC=hjoQuoteRateT.divide(new BigDecimal(taskLists.size()),0,BigDecimal.ROUND_HALF_UP);
        }
        String atime="当前";
        String btime="当前";
        SimpleDateFormat f1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat f2=new SimpleDateFormat("yyyy/MM/dd");
        if(null!=systemQueryDto.getStartTime()){
            try {
                atime=f2.format(f1.parse(systemQueryDto.getStartTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(null!=systemQueryDto.getEndTime()){
            try {
                btime=f2.format(f1.parse(systemQueryDto.getEndTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        stringBuilder.append(atime).append("至").append(btime).append(" ");
        stringBuilder.append("平均到报率为").append(hjoQuoteRateC).append("%,");
        stringBuilder.append("其中,").append(fate).append("天到报率为100%,").append(list.size()-fate).append("天存在缺报");
        stringBuilder.append(zlBl).append("到报率为100%");
        mapHjR.put("hjC",hjoQuoteRateC);
        mapHjR.put("timestamp","(日期)平均");
        list.add(mapHjR);
        Map<String,Object> mapHjJl=new HashMap<>();
        mapHjJl.put("timestamp","结论");

        if(taskLists.size()>0){
            mapHjJl.put(taskLists.get(0),stringBuilder.toString());
        }
        list.add(mapHjJl);
        Map<String, Object> result=new HashMap<>();
        result.put("tableData",list);
        result.put("mergeCells",mergeCells);
        result.put("title","资料监视文件及时率报告（"+atime+"至"+btime +")" );

        return result;

    }

    public SearchSourceBuilder buildWhere(SystemQueryDto systemQueryDto) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("d_data_time");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolBuilder);
        return searchSourceBuilder;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static SXSSFWorkbook createCSVUtilRow(String sheetName, SXSSFWorkbook wb, List<CellModel> cellModelList,
                                                 String[] headers, List<LinkedHashMap> exportData, List<ImageVo> imageVos, int st)throws Exception {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("宋体");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        style.setWrapText(true);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setWrapText(true);
        Font headerFont = wb.createFont();
        headerFont.setFontName("宋体");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        //headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);
        //设置表格名称
        Sheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth(20);
        if(null!=imageVos){
            for(int k=0;k<imageVos.size();k++){
                ImageVo imageVo=imageVos.get(k);
                CreationHelper helper = wb.getCreationHelper();
                Drawing drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();

                // 图片插入坐标
                anchor.setDx1(0);
                anchor.setDy1(0);
                anchor.setDx2(0);
                anchor.setDy2(0);
                anchor.setCol1(imageVo.getCol1());
                anchor.setCol2(imageVo.getCol2());
                anchor.setRow1(imageVo.getRow1());
                anchor.setRow2(imageVo.getRow2());
                anchor.setAnchorType(ClientAnchor.AnchorType.byId(3));
                // 插入图片
                Picture pict = drawing.createPicture(anchor, wb.addPicture(imageVo.getBytes(), HSSFWorkbook.PICTURE_TYPE_PNG));
                // 合并日期占两行(4个参数，分别为起始行，结束行，起始列，结束列)
                // 行和列都是从0开始计数，且起始结束都会合并
                // 这里是合并excel中日期的两行为一行
                CellRangeAddress region = new CellRangeAddress(imageVo.getRow1(), imageVo.getRow2()-1, imageVo.getCol1(), imageVo.getCol2()-1);
                sheet.addMergedRegion(region);
            }
            st=st+1;
        }
        Row row =  sheet.createRow(st);
        for (int i = 0; i < headers.length; i++) {
            // 遍历插入表头
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.get("header"));
        }
        int rowNum=st+1;
        for (LinkedHashMap hashMap : exportData) {
            Row rowValue = sheet.createRow(rowNum);
            rowNum++;
            Iterator<Map.Entry> iteratorRow = hashMap.entrySet().iterator();
            while (iteratorRow.hasNext()) {
                Map.Entry entryRow = iteratorRow.next();
                Integer key = Integer.valueOf(entryRow.getKey().toString());
                String value = "";
                if (entryRow.getValue() != null) {
                    value = entryRow.getValue().toString();
                } else {
                    value = "";
                }
                Cell cellValue =  rowValue.createCell(key - 1);
                cellValue.setCellValue(value);
                cellValue.setCellStyle(styles.get("data"));
            }
        }

        for (CellModel cellModel : cellModelList) {
            if(cellModel.getEndColumn()-cellModel.getStartColumn()>0||cellModel.getEndRow()-cellModel.getStartRow()>0){
                sheet.addMergedRegion(new CellRangeAddress(cellModel.getStartRow(),
                        cellModel.getEndRow(), cellModel.getStartColumn(), cellModel.getEndColumn()));
            }
        }

        return wb;
    }
    public void exportFileReportRow(SystemQueryDto systemQueryDto,String chart){
        HttpServletResponse response = ServletUtils.getResponse();

        OutputStream out = null;
        //设置最大数据行数
        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

        try {
            String  filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_文件报表.xlsx";
            List<CellModel> cellModelList = new ArrayList<>();
            Map<String, Object> fileList=this.findFileReportRow(systemQueryDto);
            List<Map<String, Object>> list= (List<Map<String, Object>>) fileList.get("tableData");
            List<Map<String,Object>> mergeCells= (List<Map<String, Object>>) fileList.get("mergeCells");
            List<LinkedHashMap> exportData = new ArrayList<LinkedHashMap>();
            if(null!=list&&list.size()>0){
                for(Map<String,Object> dataMap:list){
                    LinkedHashMap<String,String> rowPut=new LinkedHashMap<>();
                    rowPut.put("1", !String.valueOf(dataMap.get("taskName")).equals("null")?String.valueOf(dataMap.get("taskName")):"");
                    rowPut.put("2", !String.valueOf(dataMap.get("timestamp")).equals("null")?String.valueOf(dataMap.get("timestamp")):"");
                    rowPut.put("3", !String.valueOf(dataMap.get("sumRealFileNum")).equals("null")?String.valueOf(dataMap.get("sumRealFileNum")):"");
                    rowPut.put("4", !String.valueOf(dataMap.get("sumLateNum")).equals("null")?String.valueOf(dataMap.get("sumLateNum")):"");
                    rowPut.put("5", !String.valueOf(dataMap.get("sumFileNum")).equals("null")?String.valueOf(dataMap.get("sumFileNum")):"");
                    rowPut.put("6", !String.valueOf(dataMap.get("sumRealFileSize")).equals("null")?String.valueOf(dataMap.get("sumRealFileSize")):"");
                    rowPut.put("7", !String.valueOf(dataMap.get("toQuoteRate")).equals("null")?String.valueOf(dataMap.get("toQuoteRate")):"");
                    rowPut.put("8", !String.valueOf(dataMap.get("timelinessRate")).equals("null")?String.valueOf(dataMap.get("timelinessRate")):"");

                    exportData.add(rowPut);
                }
            }
            if(null!=mergeCells&&mergeCells.size()>0){
                for(Map<String,Object> dataMap:mergeCells){
                    CellModel cellModel=new CellModel();
                    cellModel.setStartColumn((Integer) dataMap.get("col"));
                    cellModel.setEndColumn((Integer) dataMap.get("col"));
                    cellModel.setStartRow((Integer) dataMap.get("row")+1);
                    cellModel.setEndRow((Integer) dataMap.get("rowspan")+cellModel.getStartRow()-1);
                    cellModelList.add(cellModel);
                }
            }
            List<ImageVo> imageVos=new ArrayList<>();
            ImageVo charIm=new ImageVo();
            byte[] charByte= ImageUtils.generateImageToByte(chart);
            charIm.setCol1(1);
            charIm.setCol2(6);
            charIm.setRow1(0);
            charIm.setRow2(18);
            charIm.setBytes(charByte);
            imageVos.add(charIm);
            String[] headers=new String[]{"名称","时间","准时到","晚到","应到","大小kb","到报率","及时率"};
            wb = this.createCSVUtilRow("文件报表",wb, cellModelList, headers, exportData,imageVos,18);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Access-Control-Expose-Headers", "content-disposition");
            response.addHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public void exportFileReportRowPdf(SystemQueryDto systemQueryDto,String chart){
        HttpServletResponse response = ServletUtils.getResponse();

        OutputStream out = null;
        //设置最大数据行数
        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

        try {
            String  filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_文件报表.xlsx";
            List<CellModel> cellModelList = new ArrayList<>();
            Map<String, Object> fileList=this.findFileReportRow(systemQueryDto);
            List<Map<String, Object>> list= (List<Map<String, Object>>) fileList.get("tableData");
            List<Map<String,Object>> mergeCells= (List<Map<String, Object>>) fileList.get("mergeCells");
            List<LinkedHashMap> exportData = new ArrayList<LinkedHashMap>();
            if(null!=list&&list.size()>0){
                for(Map<String,Object> dataMap:list){
                    LinkedHashMap<String,String> rowPut=new LinkedHashMap<>();
                    rowPut.put("1", !String.valueOf(dataMap.get("taskName")).equals("null")?String.valueOf(dataMap.get("taskName")):"");
                    rowPut.put("2", !String.valueOf(dataMap.get("timestamp")).equals("null")?String.valueOf(dataMap.get("timestamp")):"");
                    rowPut.put("3", !String.valueOf(dataMap.get("sumRealFileNum")).equals("null")?String.valueOf(dataMap.get("sumRealFileNum")):"");
                    rowPut.put("4", !String.valueOf(dataMap.get("sumLateNum")).equals("null")?String.valueOf(dataMap.get("sumLateNum")):"");
                    rowPut.put("5", !String.valueOf(dataMap.get("sumFileNum")).equals("null")?String.valueOf(dataMap.get("sumFileNum")):"");
                    rowPut.put("6", !String.valueOf(dataMap.get("sumRealFileSize")).equals("null")?String.valueOf(dataMap.get("sumRealFileSize")):"");
                    rowPut.put("7", !String.valueOf(dataMap.get("toQuoteRate")).equals("null")?String.valueOf(dataMap.get("toQuoteRate")):"");
                    rowPut.put("8", !String.valueOf(dataMap.get("timelinessRate")).equals("null")?String.valueOf(dataMap.get("timelinessRate")):"");

                    exportData.add(rowPut);
                }
            }
            if(null!=mergeCells&&mergeCells.size()>0){
                for(Map<String,Object> dataMap:mergeCells){
                    CellModel cellModel=new CellModel();
                    cellModel.setStartColumn((Integer) dataMap.get("col"));
                    cellModel.setEndColumn((Integer) dataMap.get("col"));
                    cellModel.setStartRow((Integer) dataMap.get("row")+1);
                    cellModel.setEndRow((Integer) dataMap.get("rowspan")+cellModel.getStartRow()-1);
                    cellModelList.add(cellModel);
                }
            }
            List<ImageVo> imageVos=new ArrayList<>();
            ImageVo charIm=new ImageVo();
            byte[] charByte= ImageUtils.generateImageToByte(chart);
            charIm.setCol1(1);
            charIm.setCol2(6);
            charIm.setRow1(0);
            charIm.setRow2(18);
            charIm.setBytes(charByte);
            imageVos.add(charIm);
            String[] headers=new String[]{"名称","时间","准时到","晚到","应到","大小kb","到报率","及时率"};
            wb = this.createCSVUtilRow("文件报表",wb, cellModelList, headers, exportData,imageVos,18);
            filename = filename.replaceAll(".xlsx",".pdf");
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Access-Control-Expose-Headers", "content-disposition");
            response.addHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out = response.getOutputStream();
            ExcelToPdf.excel2pdf(wb, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * 生成表格（用于生成复杂表头）
     *
     * @param sheetName sheet名称
     * @param wb 表对象
     * @param cellListMap 表头数据 {key=cellRowNum-1}
     * @param  表头总占用行数
     * @param exportData 行数据
     * @param
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static SXSSFWorkbook createCSVUtil(String sheetName, SXSSFWorkbook wb,String title,String title1,
                                              List<String> headers, List<LinkedHashMap> exportData,List<String> headers1, List<LinkedHashMap> exportData1)throws Exception {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("宋体");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        style.setWrapText(true);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setWrapText(true);
        Font headerFont = wb.createFont();
        headerFont.setFontName("宋体");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        //headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);
        //设置表格名称
        Sheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth(20);
        Row row0 =  sheet.createRow(0);
        CellRangeAddress cra =new CellRangeAddress(0,
                0, 0, headers.size()-1);
        sheet.addMergedRegion(cra);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet); // 左边框
        RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet); // 有边框
        RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet); // 上边框
        RegionUtil.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra,sheet);
        RegionUtil.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra,sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra,sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra,sheet);
        Cell cell1 = row0.createCell(0);
        cell1.setCellValue(title);
        cell1.setCellStyle(styles.get("header"));
        int st=1;
        Row row =  sheet.createRow(st);
        for (int i = 0; i < headers.size(); i++) {
            // 遍历插入表头
            Cell cell = row.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(styles.get("header"));
        }
        int rowNum=st+1;
        for (LinkedHashMap hashMap : exportData) {
            Row rowValue = sheet.createRow(rowNum);
            rowNum++;
            Iterator<Map.Entry> iteratorRow = hashMap.entrySet().iterator();
            while (iteratorRow.hasNext()) {
                Map.Entry entryRow = iteratorRow.next();
                Integer key = Integer.valueOf(entryRow.getKey().toString());
                String value = "";
                if (entryRow.getValue() != null) {
                    value = entryRow.getValue().toString();
                } else {
                    value = "";
                }
                Cell cellValue =  rowValue.createCell(key - 1);
                cellValue.setCellValue(value);
                cellValue.setCellStyle(styles.get("data"));
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(exportData.size()+1,
                exportData.size()+1, 1, headers.size()-1));
        rowNum++;
        Row rown =  sheet.createRow(rowNum);
        CellRangeAddress cra1= new CellRangeAddress( rowNum,
                rowNum, 0, headers1.size()-1);
        sheet.addMergedRegion(cra1);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cra1, sheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, cra1, sheet); // 左边框
        RegionUtil.setBorderRight(BorderStyle.THIN, cra1, sheet); // 有边框
        RegionUtil.setBorderTop(BorderStyle.THIN, cra1, sheet); // 上边框
        RegionUtil.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra1,sheet);
        RegionUtil.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra1,sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra1,sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex(),cra1,sheet);
        Cell celln = rown.createCell(0);
        celln.setCellValue(title1);
        celln.setCellStyle(styles.get("header"));
        rowNum++;
        Row row1 =  sheet.createRow(rowNum);
        rowNum++;
        for (int i = 0; i < headers1.size(); i++) {
            // 遍历插入表头
            Cell cell = row1.createCell(i);
            cell.setCellValue(headers1.get(i));
            cell.setCellStyle(styles.get("header"));
        }
        for (LinkedHashMap hashMap : exportData1) {
            Row rowValue = sheet.createRow(rowNum);
            rowNum++;
            Iterator<Map.Entry> iteratorRow = hashMap.entrySet().iterator();
            while (iteratorRow.hasNext()) {
                Map.Entry entryRow = iteratorRow.next();
                Integer key = Integer.valueOf(entryRow.getKey().toString());
                String value = "";
                if (entryRow.getValue() != null) {
                    value = entryRow.getValue().toString();
                } else {
                    value = "";
                }
                Cell cellValue =  rowValue.createCell(key - 1);
                cellValue.setCellValue(value);
                cellValue.setCellStyle(styles.get("data"));
            }
        }

        return wb;
    }
/*    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static SXSSFWorkbook createCSVUtil(String sheetName, SXSSFWorkbook wb, Map<String,List<CellModel>> cellListMap,
                                              Integer cellRowNum, List<LinkedHashMap> exportData)throws Exception {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("宋体");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        style.setWrapText(true);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setWrapText(true);
        Font headerFont = wb.createFont();
        headerFont.setFontName("宋体");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        //headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);
        //设置表格名称
        Sheet sheet = wb.createSheet(sheetName);
        for (LinkedHashMap hashMap : exportData) {
            Row rowValue = sheet.createRow(cellRowNum);
            Iterator<Map.Entry> iteratorRow = hashMap.entrySet().iterator();
            while (iteratorRow.hasNext()) {
                Map.Entry entryRow = iteratorRow.next();
                Integer key = Integer.valueOf(entryRow.getKey().toString());
                String value = "";
                if (entryRow.getValue() != null) {
                    value = entryRow.getValue().toString();
                } else {
                    value = "";
                }
                Cell cellValue =  rowValue.createCell(key - 1);
                cellValue.setCellValue(value);
                cellValue.setCellStyle(styles.get("data"));
            }
            cellRowNum++;
        }
        for(int t = 0; t < cellRowNum; t++) {
            Row row =  sheet.createRow(t);
            List<CellModel> cellNameList = cellListMap.get(String.valueOf(t));
            for (CellModel cellModel : cellNameList) {
                if(cellModel.getEndColumn()-cellModel.getStartColumn()>0||cellModel.getEndRow()-cellModel.getStartRow()>0){
                    sheet.addMergedRegion(new CellRangeAddress(cellModel.getStartRow(),
                            cellModel.getEndRow(), cellModel.getStartColumn(), cellModel.getEndColumn()));
                }
            }
            for (int i = 0; i < cellNameList.size(); i++) {
                CellModel cellModel = cellNameList.get(i);
                // 遍历插入表头
                Cell cell = row.createCell(cellModel.getStartColumn());
                cell.setCellValue(cellModel.getCellName());
                cell.setCellStyle(styles.get("header"));
            }
        }
        return wb;
    }*/
    public Map<String, String> findHeaderRow() {
        Map<String, String> map = new HashMap<>();
        List<Map<String, String>> list = new ArrayList<>();
        FileMonitorDto fileMonitorDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        List<FileMonitorDto> fileMonitorDtos = fileMonitorService.selectBySpecification(fileMonitorDto);
        for (int i = 0; i < fileMonitorDtos.size(); i++) {
            FileMonitorDto file = fileMonitorDtos.get(i);
            map.put(file.getId(), file.getTaskName());
        }
        return map;
    }
    public Map<String, Object> findFileReportRow(SystemQueryDto systemQueryDto) {
        Map<String, String> mapTaskName=this.findHeaderRow();
        List<Map<String,Object>> mergeCells=new ArrayList<>();
        SearchSourceBuilder search = this.buildWhere(systemQueryDto);
        DateHistogramAggregationBuilder dateHis = AggregationBuilders.dateHistogram("@timestamp");
        dateHis.field("d_data_time");
        dateHis.dateHistogramInterval(DateHistogramInterval.days(1));
        dateHis.format("yyyy-MM-dd HH:mm:ss");
        dateHis.timeZone(ZoneId.of("Asia/Shanghai"));
        TermsAggregationBuilder groupByTaskId = AggregationBuilders.terms("groupByTaskId").field("task_id").size(10000);
        SumAggregationBuilder sumRealFileSize = AggregationBuilders.sum("sumRealFileSize").field("real_file_size").format("0.0000");
        SumAggregationBuilder sumRealFileNum = AggregationBuilders.sum("sumRealFileNum").field("real_file_num").format("0.0000");
        SumAggregationBuilder sumLateNum = AggregationBuilders.sum("sumLateNum").field("late_num").format("0.0000");
        SumAggregationBuilder sumFileNum = AggregationBuilders.sum("sumFileNum").field("file_num").format("0.0000");
        dateHis.subAggregation(sumRealFileNum);
        dateHis.subAggregation(sumRealFileSize);
        dateHis.subAggregation(sumLateNum);
        dateHis.subAggregation(sumFileNum);
        groupByTaskId.subAggregation(dateHis);
        search.aggregation(groupByTaskId);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            SearchResponse searchResponse = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedStringTerms parsedStringTerms = aggregations.get("groupByTaskId");
            List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
            int k=0;
            if (buckets.size() > 0) {
                for (int i = 0; i < buckets.size(); i++) {
                    Terms.Bucket bucket = buckets.get(i);
                    Map<String, Aggregation> agg = bucket.getAggregations().asMap();
                    ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) agg.get("@timestamp");
                    List<? extends Histogram.Bucket> bucketSum = parsedDateHistogram.getBuckets();
                    if(bucketSum.size()>0){
                        Map<String,Object> mergeCell=new HashMap<>();
                        mergeCell.put("row",k);
                        mergeCell.put("rowspan",bucketSum.size()+1);
                        mergeCell.put("col",0);
                        mergeCell.put("colspan",1);
                        k=k+bucketSum.size()+1;
                        mergeCells.add(mergeCell);
                    }
                    if(bucketSum.size()==0){

                    }
                    long totalRealFileNumL =  0;
                    long totalLateNumL = 0;
                    long totalRealFileSizeL = 0;
                    long totalFileNumL = 0;
                    for (int j = 0; j < bucketSum.size(); j++) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("taskId", bucket.getKeyAsString());
                        if (StringUtil.isEmpty( mapTaskName.get(bucket.getKeyAsString()))){
                            continue;
                        }
                        map.put("taskName", mapTaskName.get(bucket.getKeyAsString()));
                        Histogram.Bucket bucketV = bucketSum.get(j);
                        ZonedDateTime date = (ZonedDateTime) bucketV.getKey();
                        map.put("timestamp", formatter.format(date));
                        ParsedSum sumRealFileNumV = bucketV.getAggregations().get("sumRealFileNum");
                        ParsedSum sumLateNumV = bucketV.getAggregations().get("sumLateNum");
                        ParsedSum sumRealFileSizeV = bucketV.getAggregations().get("sumRealFileSize");
                        ParsedSum sumFileNumV = bucketV.getAggregations().get("sumFileNum");
                        long sumRealFileNumL = new BigDecimal(sumRealFileNumV.getValueAsString()).longValue();
                        totalRealFileNumL+=sumRealFileNumL;
                        long sumLateNumL = new BigDecimal(sumLateNumV.getValueAsString()).longValue();
                        totalLateNumL+=sumLateNumL;
                        long sumRealFileSizeL = new BigDecimal(sumRealFileSizeV.getValueAsString()).divide(new BigDecimal(1024), 0, BigDecimal.ROUND_UP).longValue();
                        totalRealFileSizeL+=sumRealFileSizeL;
                        long sumFileNumL = new BigDecimal(sumFileNumV.getValueAsString()).longValue();
                        totalFileNumL+=sumFileNumL;
                        if (sumFileNumL > 0) {
                            float toQuoteRate = new BigDecimal(sumRealFileNumL + sumLateNumL).divide(new BigDecimal(sumFileNumL), 4, BigDecimal.ROUND_HALF_UP).floatValue();
                            float timelinessRate = new BigDecimal(sumRealFileNumL).divide(new BigDecimal(sumFileNumL), 4, BigDecimal.ROUND_HALF_UP).floatValue();
                            map.put("toQuoteRate", toQuoteRate*100);
                            map.put("timelinessRate",timelinessRate*100);
                        }else {
                            map.put("toQuoteRate", 100);
                            map.put("timelinessRate",100);
                        }
                        map.put("sumRealFileNum", sumRealFileNumL);
                        map.put("sumLateNum", sumLateNumL);
                        map.put("sumRealFileSize", sumRealFileSizeL);
                        map.put("sumFileNum", sumFileNumL);
                        list.add(map);
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("taskId", bucket.getKeyAsString());
                    if (StringUtil.isEmpty( mapTaskName.get(bucket.getKeyAsString()))){
                        continue;
                    }
                    map.put("taskName", mapTaskName.get(bucket.getKeyAsString()));
                    map.put("timestamp", "合计");
                    map.put("sumRealFileNum", totalRealFileNumL);
                    map.put("sumLateNum", totalLateNumL);
                    map.put("sumRealFileSize", totalRealFileSizeL);
                    map.put("sumFileNum", totalFileNumL);
                    if (totalFileNumL > 0) {
                        float toQuoteRate = new BigDecimal(totalRealFileNumL + totalLateNumL).divide(new BigDecimal(totalFileNumL), 4, BigDecimal.ROUND_HALF_UP).floatValue();
                        float timelinessRate = new BigDecimal(totalRealFileNumL).divide(new BigDecimal(totalFileNumL), 4, BigDecimal.ROUND_HALF_UP).floatValue();
                        map.put("toQuoteRate", toQuoteRate*100);
                        map.put("timelinessRate",timelinessRate*100);
                    }else {
                        map.put("toQuoteRate", 100);
                        map.put("timelinessRate",100);
                    }
                    list.add(map);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> result=new HashMap<>();
        result.put("tableData",list);
        result.put("mergeCells",mergeCells);
        return result;

    }
    public void exportFileReport(SystemQueryDto systemQueryDto,List<String> taskLists){
        HttpServletResponse response = ServletUtils.getResponse();

        OutputStream out = null;
        //设置最大数据行数
        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        FileMonitorDto fileMonitorDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        List<FileMonitorDto> fileMonitorDtos = fileMonitorService.selectBySpecification(fileMonitorDto);
        Map<String,String> taskNameMap=new HashMap<>();
        for (int i = 0; i < fileMonitorDtos.size(); i++) {
            FileMonitorDto file = fileMonitorDtos.get(i);
            taskNameMap.put(file.getId(),file.getTaskName());
        }
        try {
            String  filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_文件报表.xlsx";
            Map<String,List<CellModel>> map = new HashMap<String,List<CellModel>>();
            // 设置数据
            List<String> headers = new ArrayList<String>();//总占用3行
            List<String> headers1 = new ArrayList<String>();//总占用3行
            String[] hheader=new String[]{"资料名称","资料时间","采集时间","应到(个)","实到(个)","迟到(个)","应到大小(KB)","实到大小(KB)","原因","值班员备注"};
            headers1= Arrays.asList(hheader);
            headers.add("时间");
            for(int i=0;i<taskLists.size();i++){
                headers.add(taskNameMap.get(taskLists.get(i)));
            }
            headers.add("(类别)平均");
            Map<String, Object> fileMap=this.findFileReport(systemQueryDto,taskLists);
            String tilte= (String) fileMap.get("title");
            List<Map<String, Object>> fileList= (List<Map<String, Object>>) fileMap.get("tableData");
            List<LinkedHashMap> exportData = new ArrayList<LinkedHashMap>();
            for (int i = 0; i < fileList.size(); i++) {
                Map<String, Object> map1=fileList.get(i);
                LinkedHashMap<String, String> rowPut = new LinkedHashMap<String, String>();
                rowPut.put("1", String.valueOf(map1.get("timestamp")));
                int k=1;

                for(int q=0;q<taskLists.size();q++){
                    rowPut.put(String.valueOf(k+1), !String.valueOf(map1.get(taskLists.get(q))).equals("null")?String.valueOf(map1.get(taskLists.get(q))):"");
                    k=k+1;
                }
                rowPut.put(String.valueOf(k+1),String.valueOf(map1.get("hjC")));
                exportData.add(rowPut);
            }
            List<LinkedHashMap> exportData1 = new ArrayList<LinkedHashMap>();
            Map<String, Object> fileDetailMap=this.selectPageListDetail(systemQueryDto,taskLists);
            String tilte1= (String) fileDetailMap.get("title");
            List<FileStatisticsDto>  fileDetailList= (List<FileStatisticsDto>) fileDetailMap.get("tableData");
            SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < fileDetailList.size(); i++) {
                LinkedHashMap<String, String> rowPut = new LinkedHashMap<String, String>();
                FileStatisticsDto fileStatisticsDto=fileDetailList.get(i);
                rowPut.put("1",fileStatisticsDto.getTaskName());
                rowPut.put("2",format.format(fileStatisticsDto.getDdataTime()));
                rowPut.put("3",format.format(fileStatisticsDto.getStartTimeA()));
                rowPut.put("4",String.valueOf(fileStatisticsDto.getFileNum()));
                rowPut.put("5",String.valueOf(fileStatisticsDto.getRealFileNum()));
                rowPut.put("6",String.valueOf(fileStatisticsDto.getLateNum()));
                rowPut.put("7",String.valueOf(fileStatisticsDto.getFileSize()));
                rowPut.put("8",String.valueOf(fileStatisticsDto.getRealFileSize() ));
                if(null==fileStatisticsDto.getErrorReason()){
                    fileStatisticsDto.setErrorReason("");
                }
                if(null==fileStatisticsDto.getRemark()){
                    fileStatisticsDto.setRemark("");
                }
                rowPut.put("9",String.valueOf(fileStatisticsDto.getErrorReason() ));
                rowPut.put("10",String.valueOf(fileStatisticsDto.getRemark()));
                exportData1.add(rowPut);
            }

            wb = this.createCSVUtil("文件报表",wb,tilte,tilte1, headers, exportData,headers1,exportData1);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Access-Control-Expose-Headers", "content-disposition");
            response.addHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out = response.getOutputStream();
            wb.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public Map<String,Object> selectPageListDetail(SystemQueryDto systemQueryDto,List<String> taskLists) {
        Map<String, Object> result=new HashMap<>();
        SearchSourceBuilder search = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("d_data_time");
        rangeQueryBuilder.gte(systemQueryDto.getStartTime());
        rangeQueryBuilder.lte(systemQueryDto.getEndTime());
        rangeQueryBuilder.timeZone("+08:00");
        rangeQueryBuilder.format("yyyy-MM-dd HH:mm:ss");
        boolBuilder.filter(rangeQueryBuilder);
        if(taskLists.size()==0){
            boolBuilder.must(QueryBuilders.termQuery("task_id", "null"));
        }else {
            boolBuilder.must(QueryBuilders.termsQuery("task_id", taskLists));
        }
        RangeQueryBuilder perPct = QueryBuilders.rangeQuery("per_file_num");
        perPct.lt(1f);
        boolBuilder.must(perPct);
        search.query(boolBuilder).sort("d_data_time", SortOrder.DESC);
        search.trackTotalHits(true);
        try {
            search.size(10000);
            SearchResponse response = elasticSearch7Client.search(IndexNameConstant.T_MT_FILE_STATISTICS, search);
            SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
            long count = hits.getTotalHits().value;
            List<FileStatisticsDto> fileStatisticsDtos=new ArrayList<>();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> map = hit.getSourceAsMap();
                FileStatisticsDto fileStatisticsDto=new FileStatisticsDto();
                fileStatisticsDto.setId(hit.getId());
                fileStatisticsDto.setTaskId(String.valueOf(map.get("task_id")));
                fileStatisticsDto.setTaskName(String.valueOf(map.get("task_name")));
                fileStatisticsDto.setStartTimeL((Long) map.get("start_time_l"));

                fileStatisticsDto.setDdataTime(JsonParseUtil.formateToDate((String) map.get("d_data_time")));
                String status=String.valueOf(map.get("status"));
                if(StringUtil.isEmpty(status)||"null".equals(status)){
                    status="4";
                }
                if(null!=map.get("start_time_a")){
                    fileStatisticsDto.setStartTimeA(JsonParseUtil.formateToDate((String) map.get("start_time_a")));
                }
                fileStatisticsDto.setStatus(Integer.parseInt(status));
                fileStatisticsDto.setFileNum(new BigDecimal(String.valueOf(map.get("file_num"))).longValue());
                fileStatisticsDto.setFileSize(new BigDecimal(String.valueOf(map.get("file_size"))).longValue());
                fileStatisticsDto.setRealFileSize(new BigDecimal(String.valueOf(map.get("real_file_size"))).divide(new BigDecimal(1024),0,BigDecimal.ROUND_HALF_UP).longValue());
                fileStatisticsDto.setRealFileNum(new BigDecimal(String.valueOf(map.get("real_file_num"))).longValue()+new BigDecimal(String.valueOf(map.get("late_num"))).longValue());
                fileStatisticsDto.setLateNum(new BigDecimal(String.valueOf(map.get("late_num"))).longValue());
                if(null!=map.get("timeliness_rate")){
                    fileStatisticsDto.setTimelinessRate(new BigDecimal(String.valueOf(map.get("timeliness_rate"))).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()*100);
                }
                if(null!=map.get("per_file_num")){
                    fileStatisticsDto.setPerFileNum(new BigDecimal(String.valueOf(map.get("per_file_num"))).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()*100);
                }
                if(null!=map.get("error_reason")){
                    fileStatisticsDto.setErrorReason(String.valueOf(map.get("error_reason")));
                }
                if(null!=map.get("remark")){
                    fileStatisticsDto.setRemark(String.valueOf(map.get("remark")));
                }
                fileStatisticsDtos.add(fileStatisticsDto);
            }
            result.put("tableData",fileStatisticsDtos);
            String atime="当前";
            String btime="当前";
            SimpleDateFormat f1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat f2=new SimpleDateFormat("yyyy/MM/dd");
            if(null!=systemQueryDto.getStartTime()){
                try {
                    atime=f2.format(f1.parse(systemQueryDto.getStartTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(null!=systemQueryDto.getEndTime()){
                try {
                    btime=f2.format(f1.parse(systemQueryDto.getEndTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            result.put("title","资料缺报详情报告（"+atime+"至"+btime +")" );
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

    public void updateDetail(FileStatisticsDto fileStatisticsDto){
        try {
            GetResponse response = elasticSearch7Client.get(IndexNameConstant.T_MT_FILE_STATISTICS,fileStatisticsDto.getId());
            Map<String, Object> source = response.getSourceAsMap();
            source.put("remark", fileStatisticsDto.getRemark());
            source.put("error_reason", fileStatisticsDto.getErrorReason());
            elasticSearch7Client.forceInsert(IndexNameConstant.T_MT_FILE_STATISTICS,fileStatisticsDto.getId(),source);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void exportFileReportPdf(SystemQueryDto systemQueryDto,List<String> taskLists){
        HttpServletResponse response = ServletUtils.getResponse();

        OutputStream out = null;
        //设置最大数据行数
        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        FileMonitorDto fileMonitorDto = new FileMonitorDto();
        NullUtil.changeToNull(fileMonitorDto);
        List<FileMonitorDto> fileMonitorDtos = fileMonitorService.selectBySpecification(fileMonitorDto);
        Map<String,String> taskNameMap=new HashMap<>();
        for (int i = 0; i < fileMonitorDtos.size(); i++) {
            FileMonitorDto file = fileMonitorDtos.get(i);
            taskNameMap.put(file.getId(),file.getTaskName());
        }
        try {
            String  filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_文件报表.xlsx";
            Map<String,List<CellModel>> map = new HashMap<String,List<CellModel>>();
            // 设置数据
            List<String> headers = new ArrayList<String>();//总占用3行
            List<String> headers1 = new ArrayList<String>();//总占用3行
            String[] hheader=new String[]{"资料名称","资料时间","采集时间","应到(个)","实到(个)","迟到(个)","应到大小(KB)","实到大小(KB)","原因","值班员备注"};
            headers1= Arrays.asList(hheader);
            headers.add("时间");
            for(int i=0;i<taskLists.size();i++){
                headers.add(taskNameMap.get(taskLists.get(i)));
            }
            headers.add("(类别)平均");
            Map<String, Object> fileMap=this.findFileReport(systemQueryDto,taskLists);
            String tilte= (String) fileMap.get("title");
            List<Map<String, Object>> fileList= (List<Map<String, Object>>) fileMap.get("tableData");
            List<LinkedHashMap> exportData = new ArrayList<LinkedHashMap>();
            for (int i = 0; i < fileList.size(); i++) {
                Map<String, Object> map1=fileList.get(i);
                LinkedHashMap<String, String> rowPut = new LinkedHashMap<String, String>();
                rowPut.put("1", String.valueOf(map1.get("timestamp")));
                int k=1;

                for(int q=0;q<taskLists.size();q++){
                    rowPut.put(String.valueOf(k+1), !String.valueOf(map1.get(taskLists.get(q))).equals("null")?String.valueOf(map1.get(taskLists.get(q))):"");
                    k=k+1;
                }
                rowPut.put(String.valueOf(k+1),String.valueOf(map1.get("hjC")));
                exportData.add(rowPut);
            }
            List<LinkedHashMap> exportData1 = new ArrayList<LinkedHashMap>();
            Map<String, Object> fileDetailMap=this.selectPageListDetail(systemQueryDto,taskLists);
            String tilte1= (String) fileDetailMap.get("title");
            List<FileStatisticsDto>  fileDetailList= (List<FileStatisticsDto>) fileDetailMap.get("tableData");
            SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < fileDetailList.size(); i++) {
                LinkedHashMap<String, String> rowPut = new LinkedHashMap<String, String>();
                FileStatisticsDto fileStatisticsDto=fileDetailList.get(i);
                rowPut.put("1",fileStatisticsDto.getTaskName());
                rowPut.put("2",format.format(fileStatisticsDto.getDdataTime()));
                rowPut.put("3",format.format(fileStatisticsDto.getStartTimeA()));
                rowPut.put("4",String.valueOf(fileStatisticsDto.getFileNum()));
                rowPut.put("5",String.valueOf(fileStatisticsDto.getRealFileNum()));
                rowPut.put("6",String.valueOf(fileStatisticsDto.getLateNum()));
                rowPut.put("7",String.valueOf(fileStatisticsDto.getFileSize()));
                rowPut.put("8",String.valueOf(fileStatisticsDto.getRealFileSize() ));
                if(null==fileStatisticsDto.getErrorReason()){
                    fileStatisticsDto.setErrorReason("");
                }
                if(null==fileStatisticsDto.getRemark()){
                    fileStatisticsDto.setRemark("");
                }
                rowPut.put("9",String.valueOf(fileStatisticsDto.getErrorReason() ));
                rowPut.put("10",String.valueOf(fileStatisticsDto.getRemark()));
                exportData1.add(rowPut);
            }

            wb = this.createCSVUtil("文件报表",wb,tilte,tilte1, headers, exportData,headers1,exportData1);
            filename = filename.replaceAll(".xlsx",".pdf");
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Access-Control-Expose-Headers", "content-disposition");
            response.addHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            out = response.getOutputStream();
            ExcelToPdf.excel2pdf(wb, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

}

