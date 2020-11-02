package com.piesat.common.grpc.service.impl;


import com.google.protobuf.ByteString;
import com.piesat.common.grpc.service.GrpcRequest;
import com.piesat.common.grpc.service.GrpcResponse;
import com.piesat.common.grpc.service.SerializeService;
import com.piesat.common.grpc.util.ProtobufUtils;
import com.piesat.rpc.GrpcGeneral;

/**
 * ProtoStuff 序列化/反序列化工具
 */
public class ProtoStuffSerializeService implements SerializeService {

    @Override
    public GrpcRequest deserialize(GrpcGeneral.Request request) {
        return ProtobufUtils.deserialize(request.getRequest().toByteArray(), GrpcRequest.class);
    }

    @Override
    public GrpcResponse deserialize(GrpcGeneral.Response response) {
        return ProtobufUtils.deserialize(response.getResponse().toByteArray(), GrpcResponse.class);
    }

    @Override
    public ByteString serialize(GrpcResponse response) {
        return ByteString.copyFrom(ProtobufUtils.serialize(response));
    }

    @Override
    public ByteString serialize(GrpcRequest request) {
        return ByteString.copyFrom(ProtobufUtils.serialize(request));
    }

}
