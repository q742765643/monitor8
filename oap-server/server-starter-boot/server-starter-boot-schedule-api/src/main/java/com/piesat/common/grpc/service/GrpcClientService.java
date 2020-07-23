package com.piesat.common.grpc.service;

import com.google.protobuf.ByteString;
import com.piesat.common.grpc.config.ChannelUtil;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.common.grpc.util.SerializeUtils;
import com.piesat.rpc.CommonServiceGrpc;
import com.piesat.rpc.GrpcGeneral;
import io.grpc.Channel;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/11/13 14:16
 */
@Slf4j
@Service
public class GrpcClientService {

    @Autowired
    private SerializeService defaultSerializeService;
    public GrpcResponse handle(SerializeType serializeType, GrpcRequest grpcRequest,String serverName) {
        if(serverName==null){
            log.error("通道为null{},{}",grpcRequest.getClazz(),grpcRequest.getMethod());
        }
        ChannelUtil channelUtil=ChannelUtil.getInstance();
        log.info("grpc调用{}.{}",grpcRequest.getClazz(),grpcRequest.getMethod());
        CommonServiceGrpc.CommonServiceBlockingStub blockingStub=CommonServiceGrpc.newBlockingStub(channelUtil.selectChannel(serverName));
        SerializeService serializeService = SerializeUtils.getSerializeService(serializeType, this.defaultSerializeService);
        ByteString bytes = serializeService.serialize(grpcRequest);
        int value = (serializeType == null ? -1 : serializeType.getValue());
        GrpcGeneral.Request request = GrpcGeneral.Request.newBuilder().setSerialize(value).setRequest(bytes).build();
        GrpcGeneral.Response response = null;
        try{
            response = blockingStub.handle(request);
        }catch (Exception exception){
            log.warn("rpc exception: {}", exception.getMessage());
            if ("UNAVAILABLE: io exception".equals(exception.getMessage().trim())){
                response = blockingStub.handle(request);
            }
        }
        return serializeService.deserialize(response);
    }
}
