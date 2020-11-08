package com.piesat.skywalking.api.alarm;

import com.piesat.skywalking.dto.AlarmLogDto;

import java.util.List;

public interface AlarmUnService {
    public List<AlarmLogDto> selectList(AlarmLogDto query);
}
