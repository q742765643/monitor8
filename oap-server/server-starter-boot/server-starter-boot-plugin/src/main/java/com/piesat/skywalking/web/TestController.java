package com.piesat.skywalking.web;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.TestService;
import com.piesat.sso.client.util.mq.MsgPublisher;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Api(value = "测试", tags = {"测试"})
@RequestMapping("/test")
public class TestController {
    @GrpcHthtClient
    private TestService testService;
    @Autowired
    private MsgPublisher msgPublisher;
    @ApiOperation(value = "测试grpc", notes = "测试grpc")
    @GetMapping("/a")
    public ResultT<String> getTopy() {
        ResultT<String> resultT=new ResultT<>();
        testService.getTest("aaa");
        return resultT;
    }
    @ApiOperation(value = "测试定阅发布", notes = "测试定阅发布")
    @GetMapping("/b")
    public ResultT<String> testMq() {
        ResultT<String> resultT=new ResultT<>();
        msgPublisher.sendMsg("11111111111111111112");
        return resultT;
    }
}
