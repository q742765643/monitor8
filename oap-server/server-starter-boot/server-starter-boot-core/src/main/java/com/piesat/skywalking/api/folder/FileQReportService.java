package com.piesat.skywalking.api.folder;

import com.piesat.skywalking.dto.SystemQueryDto;

import java.util.List;
import java.util.Map;

public interface FileQReportService {

    public List<Map<String, String>> findHeader();

    public List<Map<String, Object>> findFileReport(SystemQueryDto systemQueryDto);

    public List<Map<String, Object>> fileLineDiagram(String taskId);

    public void exportFileReport(SystemQueryDto systemQueryDto);
}
