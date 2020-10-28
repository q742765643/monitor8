package com.piesat.common.grpc.service;

import com.google.protobuf.ByteString;
import com.piesat.rpc.GrpcGeneral;

public interface SerializeService {

    /**
     * 序列化
     */
    ByteString serialize(GrpcResponse response);

    /**
     * 序列化
     */
    ByteString serialize(GrpcRequest request);

    /**
     * 反序列化
     */
    GrpcRequest deserialize(GrpcGeneral.Request request);

    /**
     * 反序列化
     */
    GrpcResponse deserialize(GrpcGeneral.Response response);

}
