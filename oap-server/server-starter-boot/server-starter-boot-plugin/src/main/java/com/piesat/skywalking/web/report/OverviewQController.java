package com.piesat.skywalking.web.report;

import com.piesat.skywalking.api.report.OverviewQService;
import com.piesat.skywalking.dto.OverviewDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.vo.NetworkVo;
import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @ClassName : OverviewController
 * @Description : 预览接口
 * @Author : zzj
 * @Date: 2020-10-19 18:33
 */
@RestController
@Api(value="设备预览",tags = {"设备预览"})
@RequestMapping("/overview")
public class OverviewQController {
    @Autowired
    private OverviewQService overviewQService;
    @ApiOperation(value = "预览", notes = "预览")
    @GetMapping("/get")
    public ResultT<List<OverviewDto>> get(){
        ResultT<List<OverviewDto>> resultT=new ResultT<>();
        List<OverviewDto> list=overviewQService.getOverview();
        resultT.setData(list);
        return resultT;
    }
    @ApiOperation(value = "获取就绪节点", notes = "获取就绪节点")
    @GetMapping("/getNodes")
    public ResultT<Map<String,Long>> getNodes(){
        ResultT<Map<String,Long>> resultT=new ResultT<>();
        Map<String,Long> map=overviewQService.getNodes();
        resultT.setData(map);
        return resultT;
    }
}

