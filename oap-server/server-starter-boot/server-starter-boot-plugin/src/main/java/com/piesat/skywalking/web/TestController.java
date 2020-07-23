package com.piesat.skywalking.web;

import com.piesat.common.grpc.annotation.GrpcHthtClient;
import com.piesat.skywalking.api.TestService;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "测试grpc", notes = "测试grpc")
    @GetMapping("/a")
    public ResultT<String> getTopy() {
        ResultT<String> resultT=new ResultT<>();
        testService.getTest("aaa");
        return resultT;
    }
}
