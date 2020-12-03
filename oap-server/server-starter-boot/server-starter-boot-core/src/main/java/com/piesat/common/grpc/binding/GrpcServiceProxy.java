package com.piesat.common.grpc.binding;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.config.ChannelUtil;
import com.piesat.common.grpc.config.SpringUtil;
import com.piesat.common.grpc.constant.GrpcResponseStatus;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.common.grpc.exception.GrpcException;
import com.piesat.common.grpc.service.GrpcClientService;
import com.piesat.common.grpc.service.GrpcRequest;
import com.piesat.common.grpc.service.GrpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class GrpcServiceProxy<T> implements InvocationHandler {

    private Class<T> grpcService;

    private Object invoker;

    public GrpcServiceProxy(Class<T> grpcService, Object invoker) {
        this.grpcService = grpcService;
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String className = grpcService.getName();
        if ("toString".equals(methodName) && args.length == 0) {
            return className + "@" + invoker.hashCode();
        } else if ("hashCode".equals(methodName) && args.length == 0) {
            return invoker.hashCode();
        } else if ("equals".equals(methodName) && args.length == 1) {
            Object another = args[0];
            return proxy == another;
        }
        GrpcHthtService annotation = grpcService.getAnnotation(GrpcHthtService.class);
        String server = annotation.server();
        GrpcRequest request = new GrpcRequest();
        request.setClazz(className);
        request.setMethod(methodName);
        request.setArgs(args);
        SerializeType[] serializeTypeArray = annotation.serialization();
        SerializeType serializeType = null;
        if (serializeTypeArray.length > 0) {
            serializeType = serializeTypeArray[0];
        }

        GrpcClientService grpcClientService = SpringUtil.getBean(GrpcClientService.class);
        ChannelUtil channelUtil = ChannelUtil.getInstance();
        String serverName = channelUtil.getGrpcServerName().get(className);
        GrpcResponse response = null;

        response = grpcClientService.handle(serializeType, request, serverName);
        if (response == null) {
            throw new RuntimeException("grpc 调用未知异常");
        }

        //log.info("grpc{}.{},返回码{}",request.getClazz(),request.getMethod(),response.getStatus());

        if (GrpcResponseStatus.SUCCESS.getCode() != response.getStatus()) {
            if (response.getStatus() == -2) {
                throw new Exception("gprc 未发现可用服务");
            }
            Throwable throwable = response.getException();
            GrpcException exception = new GrpcException(throwable.getClass().getName() + ": " + throwable.getMessage());
            StackTraceElement[] exceptionStackTrace = exception.getStackTrace();
            StackTraceElement[] responseStackTrace = response.getStackTrace();
            StackTraceElement[] allStackTrace = Arrays.copyOf(exceptionStackTrace, exceptionStackTrace.length + responseStackTrace.length);
            System.arraycopy(responseStackTrace, 0, allStackTrace, exceptionStackTrace.length, responseStackTrace.length);
            exception.setStackTrace(allStackTrace);
            throw exception;
        }
        return response.getResult();
    }

}
