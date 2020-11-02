package com.piesat.skywalking.handler.base;

import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.ResultT;

import java.util.List;

public interface BaseShardHandler extends BaseHandler {
    public List<?> sharding(JobContext jobContext, ResultT<String> resultT);
}
