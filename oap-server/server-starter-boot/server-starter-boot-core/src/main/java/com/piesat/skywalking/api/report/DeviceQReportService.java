package com.piesat.skywalking.api.report;

import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

/**
 * @ClassName : DeviceQReportService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-19 10:35
 */
public interface DeviceQReportService {
    public PageBean getSystemReport(PageForm<HostConfigDto> pageForm);

    public void exportExcel(PageForm<HostConfigDto> pageForm);

    public void exportPdf(PageForm<HostConfigDto> pageForm);

    public void exportExcelLink(PageForm<HostConfigDto> pageForm);

    public void exportPdfLink(PageForm<HostConfigDto> pageForm);
}

