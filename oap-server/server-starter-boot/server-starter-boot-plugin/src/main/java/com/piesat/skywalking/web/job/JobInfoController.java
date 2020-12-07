package com.piesat.skywalking.web.job;

import com.piesat.skywalking.dto.model.HtJobInfoDto;
import com.piesat.skywalking.service.timing.JobInfoService;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @ClassName : JobInfoController
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-15 18:16
 */
@RestController
@Api(value = "通用任务接口", tags = {"通用任务接口"})
@RequestMapping("/jobInfo")
public class JobInfoController {
    @Autowired
    private JobInfoService jobInfoService;

    @ApiOperation(value = "分页查询任务", notes = "分页查询任务")
    @GetMapping("/list")
    public ResultT<PageBean<HtJobInfoDto>> list(HtJobInfoDto htJobInfoDto, int pageNum, int pageSize) {
        ResultT<PageBean<HtJobInfoDto>> resultT = new ResultT<>();
        PageForm<HtJobInfoDto> pageForm = new PageForm<>(pageNum, pageSize, htJobInfoDto);
        PageBean pageBean = jobInfoService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询任务", notes = "根据ID查询任务")
    @GetMapping(value = "/{id:.+}")
    public ResultT<HtJobInfoDto> getInfo(@PathVariable String id) {
        ResultT<HtJobInfoDto> resultT = new ResultT<>();
        HtJobInfoDto htJobInfoDto = jobInfoService.findById(id);
        resultT.setData(htJobInfoDto);
        return resultT;
    }

    @ApiOperation(value = "添加任务或者修改任务", notes = "添加任务或者修改任务")
    @PostMapping
    @RequiresPermissions("jobInfo:jobInfo:add")
    @Log(title = "通用调度管理", businessType = BusinessType.INSERT)
    public ResultT<String> add(@RequestBody HtJobInfoDto htJobInfoDto) {
        ResultT<String> resultT = new ResultT<>();
        jobInfoService.save(htJobInfoDto);
        return resultT;
    }

    @ApiOperation(value = "删除任务", notes = "删除任务")
    @DeleteMapping("/{ids:.+}")
    @RequiresPermissions("jobInfo:jobInfo:remove")
    @Log(title = "通用调度管理", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        jobInfoService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }
    @GetMapping(value = "/getNextTime")
    @ApiOperation(value = "计算下5次执行时间", notes = "计算下5次执行时间")
    public ResultT<List<String>> getNextTime(String cronExpression){
        ResultT< List<String>> resultT=new ResultT<>();
        List<String> cronTimeList = new ArrayList<>();
        try {
            CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
            Date nextTimePoint = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < 5; i++) {
                nextTimePoint = cronSequenceGenerator.next(nextTimePoint);
                cronTimeList.add(sdf.format(nextTimePoint));
            }
            resultT.setData(cronTimeList);
        } catch (Exception e) {
            resultT.setErrorMessage("表达式错误");
            e.printStackTrace();
        }
        return resultT;
    }

    @ApiOperation(value = "启动停止", notes = "启动停止")
    @PostMapping("/updateJob")
    @RequiresPermissions("jobInfo:jobInfo:updateJob")
    @Log(title = "通用调度管理", businessType = BusinessType.UPDATE)
    public ResultT<String> updateJob(@RequestBody HtJobInfoDto htJobInfoDto) {
        ResultT<String> resultT = new ResultT<>();
        jobInfoService.updateJob(htJobInfoDto);
        return resultT;
    }

    @ApiOperation(value = "立即执行", notes = "立即执行")
    @GetMapping("/trigger/{id:.+}")
    @RequiresPermissions("jobInfo:jobInfo:trigger")
    public ResultT<String> trigger(@PathVariable("id") String id){
        ResultT<String> resultT = new ResultT<>();
        jobInfoService.trigger(id);
        return resultT;
    }
}

