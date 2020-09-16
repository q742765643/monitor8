package com.piesat.common.grpc.service;

import com.google.protobuf.ByteString;
import com.piesat.common.grpc.config.ChannelUtil;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.common.grpc.util.SerializeUtils;
import com.piesat.rpc.CommonServiceGrpc;
import com.piesat.rpc.GrpcGeneral;
import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.dto.model.JobContext;
import com.piesat.util.StringUtil;
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
        Object[] args=grpcRequest.getArgs();
        HtJobInfoDto htJobInfoDto=null;
        if(args.length>0&&args[0] instanceof JobContext){
            JobContext jobContext= (JobContext) args[0];
            htJobInfoDto= (HtJobInfoDto) jobContext.getHtJobInfoDto();
        }
        Channel channel=null;
        try {
            if(null!=htJobInfoDto&&!StringUtil.isEmpty(htJobInfoDto.getAddress())){
                channel=channelUtil.selectChannel(serverName,htJobInfoDto.getAddress());
            }else {
                channel=channelUtil.selectChannel(serverName);
            }
        } catch (Exception e) {
            log.error("通道为null{},{}",grpcRequest.getClazz(),grpcRequest.getMethod());
            GrpcResponse response=new GrpcResponse();
            response.setStatus(-2);
            response.setMessage("grpc无服务异常");
            return response;
        }
        CommonServiceGrpc.CommonServiceBlockingStub blockingStub=CommonServiceGrpc.newBlockingStub(channel);
        SerializeService serializeService = SerializeUtils.getSerializeService(serializeType, this.defaultSerializeService);
        ByteString bytes = serializeService.serialize(grpcRequest);
        int value = (serializeType == null ? -1 : serializeType.getValue());
        GrpcGeneral.Request request = GrpcGeneral.Request.newBuilder().setSerialize(value).setRequest(bytes).build();
        GrpcGeneral.Response response = null;
        try{
            response = blockingStub.handle(request);
        }catch (Exception exception){
            log.error("rpc exception: {}", exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
       /* try{
            response = blockingStub.handle(request);
        }catch (Exception exception){
            log.error("rpc exception: {}", exception.getMessage());
            GrpcResponse response1=new GrpcResponse();
            response1.setStatus(-1);
            String message = exception.getCause().getClass().getName() + ": " + exception.getCause().getMessage();
            response1.error(message, exception.getCause(), exception.getCause().getStackTrace());
            return response1;

        }*/
        return serializeService.deserialize(response);
    }
}
