package com.piesat.skywalking.api.report;

import com.piesat.skywalking.dto.OverviewDto;

import java.util.List;
import java.util.Map;

/**
 * @ClassName : OverviewService
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-19 18:34
 */

public interface OverviewQService {
    public List<OverviewDto> getOverview();

    public Map<String,Long> getNodes();
}

