package com.piesat.skywalking.web.report;

import com.piesat.skywalking.api.report.OverviewQService;
import com.piesat.skywalking.dto.OverviewDto;
import com.piesat.skywalking.dto.SystemQueryDto;
import com.piesat.skywalking.dto.model.NodeStatusDto;
import com.piesat.skywalking.dto.model.OverviewNodeDto;
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
    public ResultT<OverviewNodeDto> getNodes(){
        ResultT<OverviewNodeDto> resultT=new ResultT<>();
        OverviewNodeDto overviewNodeDto=overviewQService.getNodes();
        resultT.setData(overviewNodeDto);
        return resultT;
    }

    @ApiOperation(value = "获取节点总状态", notes = "获取节点总状态")
    @GetMapping("/getNodesStatus")
    public  ResultT<NodeStatusDto> getNodesStatus(){
        ResultT<NodeStatusDto> resultT=new ResultT<>();
        NodeStatusDto nodeStatusDto=overviewQService.getNodesStatus();
        resultT.setData(nodeStatusDto);
        return resultT;
    }
}

