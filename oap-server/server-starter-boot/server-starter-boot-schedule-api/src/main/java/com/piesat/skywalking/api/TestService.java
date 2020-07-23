package com.piesat.skywalking.api;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.util.constant.GrpcConstant;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_CLIENT_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface TestService {
    public String getTest(String aa);
}
