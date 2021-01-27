package com.piesat.skywalking.web.report;

import com.piesat.skywalking.api.folder.FileQReportService;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResultT<List<Map<String, Object>>> findFileReport(SystemQueryDto systemQueryDto) {
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
    @ApiOperation(value = "导出文件报表", notes = "导出文件报表")
    @GetMapping("/exportFileReport")
    public void exportFileReport(SystemQueryDto systemQueryDto){
        fileQReportService.exportFileReport(systemQueryDto);
    }

    @ApiOperation(value = "文件报表-按行合并", notes = "文件报表-按行合并")
    @GetMapping("/findFileReportRow")
    public ResultT<Map<String, Object> > findFileReportRow(SystemQueryDto systemQueryDto) {
        ResultT<Map<String, Object> > resultT = new ResultT<>();
        Map<String, Object>  map = fileQReportService.findFileReportRow(systemQueryDto);
        resultT.setData(map);
        return resultT;
    }

    @ApiOperation(value = "导出文件报表-按行合并", notes = "导出文件报表-按行合并")
    @PostMapping("/exportFileReportRow")
    public void exportFileReportRow(SystemQueryDto systemQueryDto,String chart){
        fileQReportService.exportFileReportRow(systemQueryDto,chart);
    }
    @ApiOperation(value = "导出文件报表-按行合并", notes = "导出文件报表-按行合并")
    @PostMapping("/exportFileReportRowPdf")
    public void exportFileReportRowPdf(SystemQueryDto systemQueryDto,String chart){
        fileQReportService.exportFileReportRowPdf(systemQueryDto,chart);
    }

    @ApiOperation(value = "文件报表折线图-到报率查询", notes = "文件报表折线图-到报率查询 " +
            "\n参数为endTime 和beginTime"+
            "随文件报表时间查询框变动" )
    @GetMapping("/findFileReportLineChart")
    public  ResultT<Map<String, Object> > findFileReportLineChart(SystemQueryDto systemQueryDto){
        ResultT<Map<String, Object> > resultT = new ResultT<>();
        Map<String, Object>  map = fileQReportService.findFileReportLineChart(systemQueryDto);
        resultT.setData(map);
        return resultT;
    }
}

