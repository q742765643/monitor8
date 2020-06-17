package com.piesat.skywalking.web;

import com.piesat.skywalking.om.protocol.snmp.SNMPService;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value="snmp获取系统信息",tags = {"snmp获取系统信息"})
public class SNMPController {
    @Autowired
    private SNMPService snmpService;
    @GetMapping("/snmp/get")
    public ResultT<String> snmp() {
      ResultT<String> resultT=new ResultT<>();
        snmpService.getSystemInfo("10.1.100.96","161","2");
      return resultT;
    }
}
