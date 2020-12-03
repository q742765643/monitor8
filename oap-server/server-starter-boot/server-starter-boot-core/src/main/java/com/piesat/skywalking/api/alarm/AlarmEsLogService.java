package com.piesat.skywalking.api.alarm;

import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;
import java.util.Map;

public interface AlarmEsLogService {
    public PageBean selectPageList(PageForm<AlarmLogDto> pageForm);

    public Map<String,Object> selectAlarmTrend(AlarmLogDto query);

    public List<Map<String,Object>> selectAlarmLevel(AlarmLogDto query);

    public List<Map<String,Object>> selectAlarmList(AlarmLogDto query);

    public Map<String,Long> selectAlarmNum(AlarmLogDto query);

    public void deleteAlarm(List<String> ids);
}
