package com.piesat.skywalking.web.folder;

import com.piesat.skywalking.service.folder.FolderMonitorService;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value="文件监控测试接口",tags = {"文件监控测试接口"})
@RequestMapping("/folderMonitor")
public class FolderMonitorController {
    @Autowired
    private FolderMonitorService folderMonitorService;

    @ApiOperation(value = "文件监控启动", notes = "文件监控启动")
    @GetMapping("/start")
    public ResultT<String> start(String path){
        ResultT<String> resultT=new ResultT<>();
        folderMonitorService.start(path);
        return resultT;
    }
}
