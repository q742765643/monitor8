package com.piesat.skywalking.web.system;

import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.service.system.SystemService;
import com.piesat.skywalking.vo.*;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(value="查询系统信息",tags = {"查询系统信息"})
@RequestMapping("/system")
public class SystemController {
    @Autowired
    private SystemService systemService;

    @ApiOperation(value = "查询网速", notes = "查询网速")
    @GetMapping("/getNetwork")
    public ResultT<List<NetworkVo>> getNetwork(SystemQueryDto systemQueryDto){
        ResultT<List<NetworkVo>> resultT=new ResultT<>();
        List<NetworkVo> list=systemService.getNetwork(systemQueryDto);
        resultT.setData(list);
        return resultT;
    }
    @ApiOperation(value = "查询磁盘IO", notes = "查询磁盘IO")
    @GetMapping("/getDiskio")
    public ResultT<List<DiskioVo>> getDiskio(SystemQueryDto systemQueryDto){
        ResultT<List<DiskioVo>> resultT=new ResultT<>();
        List<DiskioVo> list=systemService.getDiskio(systemQueryDto);
        resultT.setData(list);
        return resultT;
    }
    @ApiOperation(value = "查询CPU", notes = "查询CPU")
    @GetMapping("/getCpu")
    public ResultT<List<CpuVo>> getCpu(SystemQueryDto systemQueryDto){
        ResultT<List<CpuVo>> resultT=new ResultT<>();
        List<CpuVo> list=systemService.getCpu(systemQueryDto);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询内存", notes = "查询内存")
    @GetMapping("/getMemory")
    public ResultT<List<MemoryVo>> getMemory(SystemQueryDto systemQueryDto){
        ResultT<List<MemoryVo>> resultT=new ResultT<>();
        List<MemoryVo> list=systemService.getMemory(systemQueryDto);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询磁盘文件系统", notes = "查询磁盘文件系统")
    @GetMapping("/getFileSystem")
    public ResultT<List<FileSystemVo>> getFileSystem(SystemQueryDto systemQueryDto){
        ResultT<List<FileSystemVo>> resultT=new ResultT<>();
        List<FileSystemVo> list=systemService.getFileSystem(systemQueryDto);
        resultT.setData(list);
        return resultT;
    }
    @ApiOperation(value = "查询进程", notes = "查询进程")
    @GetMapping("/getProcess")
    public ResultT<List<ProcessVo>> getProcess(SystemQueryDto systemQueryDto){
        ResultT<List<ProcessVo>> resultT=new ResultT<>();
        List<ProcessVo> list=systemService.getProcess(systemQueryDto);
        resultT.setData(list);
        return resultT;
    }
}
