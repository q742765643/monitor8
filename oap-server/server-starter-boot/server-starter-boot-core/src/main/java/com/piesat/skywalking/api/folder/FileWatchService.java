package com.piesat.skywalking.api.folder;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.util.constant.GrpcConstant;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_CLIENT_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface FileWatchService {
    public void start(String path);
}
