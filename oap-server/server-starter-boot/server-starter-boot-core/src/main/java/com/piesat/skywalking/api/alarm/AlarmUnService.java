package com.piesat.skywalking.api.alarm;

import com.piesat.skywalking.dto.AlarmLogDto;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

public interface AlarmUnService {
    public List<AlarmLogDto> selectList(AlarmLogDto query);

    public PageBean selectPageList(PageForm<AlarmLogDto> pageForm);
}
