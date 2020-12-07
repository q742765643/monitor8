package com.piesat.skywalking.api.report;

import com.piesat.skywalking.dto.AlarmConfigDto;

import java.util.List;

public interface AlarmQReportService {
    public Object getAlarmReport(AlarmConfigDto alarmConfigDto);

    public void exportExcel(AlarmConfigDto alarmConfigDto);
}
