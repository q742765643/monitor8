package com.piesat.skywalking.handler.base;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.ResultT;

import java.util.List;
import java.util.Map;

public interface BaseShardHandler extends BaseHandler{
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT);
}
