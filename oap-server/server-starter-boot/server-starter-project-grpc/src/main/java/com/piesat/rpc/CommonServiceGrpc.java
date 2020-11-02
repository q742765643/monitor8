package com.piesat.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * 定义通用的 Grpc 服务
 * </pre>
 */
@javax.annotation.Generated(
        value = "by gRPC proto compiler (version 1.27.1)",
        comments = "Source: service.proto")
public final class CommonServiceGrpc {

    public static final String SERVICE_NAME = "CommonService";
    private static final int METHODID_HANDLE = 0;
    // Static method descriptors that strictly reflect the proto.
    private static volatile io.grpc.MethodDescriptor<com.piesat.rpc.GrpcGeneral.Request,
            com.piesat.rpc.GrpcGeneral.Response> getHandleMethod;
    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    private CommonServiceGrpc() {
    }

    @io.grpc.stub.annotations.RpcMethod(
            fullMethodName = SERVICE_NAME + '/' + "handle",
            requestType = com.piesat.rpc.GrpcGeneral.Request.class,
            responseType = com.piesat.rpc.GrpcGeneral.Response.class,
            methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<com.piesat.rpc.GrpcGeneral.Request,
            com.piesat.rpc.GrpcGeneral.Response> getHandleMethod() {
        io.grpc.MethodDescriptor<com.piesat.rpc.GrpcGeneral.Request, com.piesat.rpc.GrpcGeneral.Response> getHandleMethod;
        if ((getHandleMethod = CommonServiceGrpc.getHandleMethod) == null) {
            synchronized (CommonServiceGrpc.class) {
                if ((getHandleMethod = CommonServiceGrpc.getHandleMethod) == null) {
                    CommonServiceGrpc.getHandleMethod = getHandleMethod =
                            io.grpc.MethodDescriptor.<com.piesat.rpc.GrpcGeneral.Request, com.piesat.rpc.GrpcGeneral.Response>newBuilder()
                                    .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                                    .setFullMethodName(generateFullMethodName(SERVICE_NAME, "handle"))
                                    .setSampledToLocalTracing(true)
                                    .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            com.piesat.rpc.GrpcGeneral.Request.getDefaultInstance()))
                                    .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                                            com.piesat.rpc.GrpcGeneral.Response.getDefaultInstance()))
                                    .setSchemaDescriptor(new CommonServiceMethodDescriptorSupplier("handle"))
                                    .build();
                }
            }
        }
        return getHandleMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static CommonServiceStub newStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<CommonServiceStub> factory =
                new io.grpc.stub.AbstractStub.StubFactory<CommonServiceStub>() {
                    @java.lang.Override
                    public CommonServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                        return new CommonServiceStub(channel, callOptions);
                    }
                };
        return CommonServiceStub.newStub(factory, channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static CommonServiceBlockingStub newBlockingStub(
            io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<CommonServiceBlockingStub> factory =
                new io.grpc.stub.AbstractStub.StubFactory<CommonServiceBlockingStub>() {
                    @java.lang.Override
                    public CommonServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                        return new CommonServiceBlockingStub(channel, callOptions);
                    }
                };
        return CommonServiceBlockingStub.newStub(factory, channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static CommonServiceFutureStub newFutureStub(
            io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<CommonServiceFutureStub> factory =
                new io.grpc.stub.AbstractStub.StubFactory<CommonServiceFutureStub>() {
                    @java.lang.Override
                    public CommonServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                        return new CommonServiceFutureStub(channel, callOptions);
                    }
                };
        return CommonServiceFutureStub.newStub(factory, channel);
    }

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (CommonServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
                            .setSchemaDescriptor(new CommonServiceFileDescriptorSupplier())
                            .addMethod(getHandleMethod())
                            .build();
                }
            }
        }
        return result;
    }

    /**
     * <pre>
     * 定义通用的 Grpc 服务
     * </pre>
     */
    public static abstract class CommonServiceImplBase implements io.grpc.BindableService {

        /**
         * <pre>
         * 处理请求
         * </pre>
         */
        public void handle(com.piesat.rpc.GrpcGeneral.Request request,
                           io.grpc.stub.StreamObserver<com.piesat.rpc.GrpcGeneral.Response> responseObserver) {
            asyncUnimplementedUnaryCall(getHandleMethod(), responseObserver);
        }

        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            getHandleMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            com.piesat.rpc.GrpcGeneral.Request,
                                            com.piesat.rpc.GrpcGeneral.Response>(
                                            this, METHODID_HANDLE)))
                    .build();
        }
    }

    /**
     * <pre>
     * 定义通用的 Grpc 服务
     * </pre>
     */
    public static final class CommonServiceStub extends io.grpc.stub.AbstractAsyncStub<CommonServiceStub> {
        private CommonServiceStub(
                io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected CommonServiceStub build(
                io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CommonServiceStub(channel, callOptions);
        }

        /**
         * <pre>
         * 处理请求
         * </pre>
         */
        public void handle(com.piesat.rpc.GrpcGeneral.Request request,
                           io.grpc.stub.StreamObserver<com.piesat.rpc.GrpcGeneral.Response> responseObserver) {
            asyncUnaryCall(
                    getChannel().newCall(getHandleMethod(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     * <pre>
     * 定义通用的 Grpc 服务
     * </pre>
     */
    public static final class CommonServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<CommonServiceBlockingStub> {
        private CommonServiceBlockingStub(
                io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected CommonServiceBlockingStub build(
                io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CommonServiceBlockingStub(channel, callOptions);
        }

        /**
         * <pre>
         * 处理请求
         * </pre>
         */
        public com.piesat.rpc.GrpcGeneral.Response handle(com.piesat.rpc.GrpcGeneral.Request request) {
            return blockingUnaryCall(
                    getChannel(), getHandleMethod(), getCallOptions(), request);
        }
    }

    /**
     * <pre>
     * 定义通用的 Grpc 服务
     * </pre>
     */
    public static final class CommonServiceFutureStub extends io.grpc.stub.AbstractFutureStub<CommonServiceFutureStub> {
        private CommonServiceFutureStub(
                io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected CommonServiceFutureStub build(
                io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CommonServiceFutureStub(channel, callOptions);
        }

        /**
         * <pre>
         * 处理请求
         * </pre>
         */
        public com.google.common.util.concurrent.ListenableFuture<com.piesat.rpc.GrpcGeneral.Response> handle(
                com.piesat.rpc.GrpcGeneral.Request request) {
            return futureUnaryCall(
                    getChannel().newCall(getHandleMethod(), getCallOptions()), request);
        }
    }

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
        private final CommonServiceImplBase serviceImpl;
        private final int methodId;

        MethodHandlers(CommonServiceImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_HANDLE:
                    serviceImpl.handle((com.piesat.rpc.GrpcGeneral.Request) request,
                            (io.grpc.stub.StreamObserver<com.piesat.rpc.GrpcGeneral.Response>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(
                io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    private static abstract class CommonServiceBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
        CommonServiceBaseDescriptorSupplier() {
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return com.piesat.rpc.GrpcGeneral.getDescriptor();
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("CommonService");
        }
    }

    private static final class CommonServiceFileDescriptorSupplier
            extends CommonServiceBaseDescriptorSupplier {
        CommonServiceFileDescriptorSupplier() {
        }
    }

    private static final class CommonServiceMethodDescriptorSupplier
            extends CommonServiceBaseDescriptorSupplier
            implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
        private final String methodName;

        CommonServiceMethodDescriptorSupplier(String methodName) {
            this.methodName = methodName;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }
}
