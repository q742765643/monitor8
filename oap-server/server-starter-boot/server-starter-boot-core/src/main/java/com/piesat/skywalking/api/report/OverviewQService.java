package com.piesat.skywalking.api.report;

import com.piesat.skywalking.dto.OverviewDto;
import com.piesat.skywalking.dto.model.NodeStatusDto;
import com.piesat.skywalking.dto.model.OverviewNodeDto;

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

    public OverviewNodeDto getNodes();


    public NodeStatusDto getNodesStatus();
}

