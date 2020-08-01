package com.piesat.skywalking.api.alarm;

import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

public interface AlarmEsLogService {
    public PageBean selectPageList(PageForm<AlarmLogDto> pageForm);
}
