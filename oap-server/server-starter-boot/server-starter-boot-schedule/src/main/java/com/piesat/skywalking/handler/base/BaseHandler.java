package com.piesat.skywalking.handler.base;

import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.ResultT;

public interface BaseHandler {
    public void execute(JobContext jobContext, ResultT<String> resultT);
}
