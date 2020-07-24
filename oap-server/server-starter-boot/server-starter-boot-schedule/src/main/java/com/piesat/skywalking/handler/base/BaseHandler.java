package com.piesat.skywalking.handler.base;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.ResultT;

import java.util.List;

public interface BaseHandler {
    public void execute(JobContext jobContext, ResultT<String> resultT);
}
