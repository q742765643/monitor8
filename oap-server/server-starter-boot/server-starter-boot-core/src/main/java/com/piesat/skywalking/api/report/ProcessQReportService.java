package com.piesat.skywalking.api.report;

import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.ProcessReportDto;

import java.util.List;

/**
 * @ClassName : ProcessQReportService
 * @Description :
 * @Author : zzj
 * @Date: 2020-12-03 10:21
 */
public interface ProcessQReportService {
    public List<ProcessReportDto> getProcessReport(ProcessConfigDto query);

    public void exportExcel(ProcessConfigDto query);

    public void exportPdf(ProcessConfigDto query);
}

