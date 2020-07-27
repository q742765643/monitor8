package com.piesat.skywalking.api.remote;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.ResultT;
import com.piesat.util.constant.GrpcConstant;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_CLIENT_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface RemoteService {

    public List<?> sharding(JobContext jobContext, ResultT<String> resultT);
    public void execute(JobContext jobContext, ResultT<String> resultT);
}
