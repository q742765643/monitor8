package com.piesat.skywalking.api.common;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.util.constant.GrpcConstant;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface OverdueCleanService {
    public int deleteRecord(String table,String endTime);
}
