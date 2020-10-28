package com.piesat.common.grpc.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.google.protobuf.ByteString;
import com.piesat.common.grpc.service.GrpcRequest;
import com.piesat.common.grpc.service.GrpcResponse;
import com.piesat.common.grpc.service.SerializeService;
import com.piesat.rpc.GrpcGeneral;

/**
 * FastJSON 序列化/反序列化工具
 */
public class FastJSONSerializeService implements SerializeService {

    @Override
    public ByteString serialize(GrpcResponse response) {
        return ByteString.copyFrom(JSON.toJSONBytes(response));
    }

    @Override
    public ByteString serialize(GrpcRequest request) {
        return ByteString.copyFrom(JSON.toJSONBytes(request));
    }

    @Override
    public GrpcRequest deserialize(GrpcGeneral.Request request) {
        byte[] bytes = request.getRequest().toByteArray();
        return JSON.parseObject(bytes, GrpcRequest.class, Feature.OrderedField);
    }

    @Override
    public GrpcResponse deserialize(GrpcGeneral.Response response) {
        byte[] bytes = response.getResponse().toByteArray();
        return JSON.parseObject(bytes, GrpcResponse.class, Feature.OrderedField);
    }

}
