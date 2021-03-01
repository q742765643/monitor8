package com.piesat.skywalking.api.folder;

import com.piesat.skywalking.dto.FileStatisticsDto;
import com.piesat.skywalking.dto.SystemQueryDto;

import java.util.List;
import java.util.Map;

public interface FileQReportService {

    public List<Map<String, String>> findHeader();

    public Map<String, Object> findFileReport(SystemQueryDto systemQueryDto,List<String> taskLists);

    public List<Map<String, Object>> fileLineDiagram(String taskId);

    public void exportFileReport(SystemQueryDto systemQueryDto,List<String> taskLists);

    public Map<String, Object>  findFileReportRow(SystemQueryDto systemQueryDto);

    public void exportFileReportRow(SystemQueryDto systemQueryDto,String chart);

    public void exportFileReportRowPdf(SystemQueryDto systemQueryDto,String chart);

    public Map<String, Object> findFileReportLineChart(SystemQueryDto systemQueryDto);

    public Map<String,Object> selectPageListDetail(SystemQueryDto systemQueryDto,List<String> taskLists);

    public void updateDetail(FileStatisticsDto fileStatisticsDto);

    public void exportFileReportPdf(SystemQueryDto systemQueryDto,List<String> taskLists);
}
