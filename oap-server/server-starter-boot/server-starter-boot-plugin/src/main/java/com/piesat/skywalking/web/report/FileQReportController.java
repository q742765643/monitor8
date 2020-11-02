package com.piesat.skywalking.web.report;

import com.piesat.skywalking.api.folder.FileQReportService;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @ClassName : FileQReportController
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-29 09:43
 */
@RestController
@Api(value = "文件报表", tags = {"文件报表"})
@RequestMapping("/fileQReport")
public class FileQReportController {
    @Autowired
    private FileQReportService fileQReportService;

    @ApiOperation(value = "查询表头", notes = "查询表头")
    @GetMapping("/findHeader")
    public ResultT<List<Map<String, String>>> findHeader() {
        ResultT<List<Map<String, String>>> resultT = new ResultT<>();
        List<Map<String, String>> list = fileQReportService.findHeader();
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "文件报表", notes = "文件报表")
    @GetMapping("/findFileReport")
    public ResultT<List<Map<String, Object>>> findFileReport(@RequestBody SystemQueryDto systemQueryDto) {
        ResultT<List<Map<String, Object>>> resultT = new ResultT<>();
        List<Map<String, Object>> list = fileQReportService.findFileReport(systemQueryDto);
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "查询折线图", notes = "查询折线图")
    @GetMapping("/fileLineDiagram")
    public ResultT<List<Map<String, Object>>> fileLineDiagram(String taskId) {
        ResultT<List<Map<String, Object>>> resultT = new ResultT<>();
        List<Map<String, Object>> list = fileQReportService.fileLineDiagram(taskId);
        resultT.setData(list);
        return resultT;
    }
}

