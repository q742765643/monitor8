package com.piesat.skywalking.web.alarm;

import com.piesat.enums.MonitorConditionEnum;
import com.piesat.enums.MonitorTypeEnum;
import com.piesat.skywalking.api.alarm.AlarmConfigService;
import com.piesat.skywalking.dto.AlarmConfigDto;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.util.ResultT;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Api(value = "告警配置接口", tags = {"告警配置接口"})
@RequestMapping("/alarmCofig")
public class AlarmCofigController {
    @Autowired
    private AlarmConfigService alarmConfigService;

    @ApiOperation(value = "分页查询告警配置", notes = "分页查询告警配置")
    @GetMapping("/list")
    public ResultT<PageBean<AlarmConfigDto>> list(AlarmConfigDto alarmConfigDto, int pageNum, int pageSize) {
        ResultT<PageBean<AlarmConfigDto>> resultT = new ResultT<>();
        PageForm<AlarmConfigDto> pageForm = new PageForm<>(pageNum, pageSize, alarmConfigDto);
        PageBean pageBean = alarmConfigService.selectPageList(pageForm);
        resultT.setData(pageBean);
        return resultT;
    }

    @ApiOperation(value = "根据ID查询告警配置", notes = "根据ID查询告警配置")
    @GetMapping(value = "/{id}")
    public ResultT<AlarmConfigDto> getInfo(@PathVariable String id) {
        ResultT<AlarmConfigDto> resultT = new ResultT<>();
        AlarmConfigDto alarmConfigDto = alarmConfigService.findById(id);
        resultT.setData(alarmConfigDto);
        return resultT;
    }

    @ApiOperation(value = "添加或者修改告警配置", notes = "添加或者修改告警配置")
    @RequiresPermissions("alarm:alarmCofig:add")
    @Log(title = "告警管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResultT<String> add(@RequestBody AlarmConfigDto alarmConfigDto) {
        ResultT<String> resultT = new ResultT<>();
        alarmConfigService.save(alarmConfigDto);
        resultT.setMessage("每种监测类型只能存在一条,相同监测类型会被覆盖");
        return resultT;
    }

    @ApiOperation(value = "删除告警配置", notes = "删除告警配置")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("alarm:alarmCofig:remove")
    @Log(title = "告警管理", businessType = BusinessType.DELETE)
    public ResultT<String> remove(@PathVariable String[] ids) {
        ResultT<String> resultT = new ResultT<>();
        alarmConfigService.deleteByIds(Arrays.asList(ids));
        return resultT;
    }

    @ApiOperation(value = "获取告警监测类型", notes = "获取告警监测类型")
    @GetMapping("/monitorType")
    public ResultT<List<Map<Integer, String>>> monitorType() {
        ResultT<List<Map<Integer, String>>> resultT = new ResultT<>();
        MonitorTypeEnum[] option = MonitorTypeEnum.values();
        List<Map<Integer, String>> list = new ArrayList<>();
        for (int i = 0; i < option.length; i++) {
            MonitorTypeEnum menum = option[i];
            Map<Integer, String> map = new HashMap<>();
            map.put(menum.getValue(), menum.getTitle());
            list.add(map);
        }
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "获取告警符号类型", notes = "获取告警符号类型")
    @GetMapping("/monitorCondition")
    public ResultT<List<Map<String, String>>> monitorCondition() {
        ResultT<List<Map<String, String>>> resultT = new ResultT<>();
        MonitorConditionEnum[] option = MonitorConditionEnum.values();
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < option.length; i++) {
            MonitorConditionEnum menum = option[i];
            Map<String, String> map = new HashMap<>();
            map.put("key", menum.name());
            map.put("value", menum.getTitle());
            list.add(map);
        }
        resultT.setData(list);
        return resultT;
    }

    @ApiOperation(value = "启动停止", notes = "启动停止")
    @PostMapping("/updateAlarm")
    @RequiresPermissions("alarm:alarmCofig:updateAlarm")
    @Log(title = "告警管理", businessType = BusinessType.UPDATE)
    public ResultT<String> updateAlarm(@RequestBody AlarmConfigDto alarmConfigDto) {
        ResultT<String> resultT = new ResultT<>();
        alarmConfigService.updateAlarm(alarmConfigDto);
        return resultT;
    }

    @ApiOperation(value = "立即执行", notes = "立即执行")
    @GetMapping("/trigger/{id:.+}")
    @RequiresPermissions("alarm:alarmCofig:trigger")
    public ResultT<String> trigger(@PathVariable("id") String id){
        ResultT<String> resultT = new ResultT<>();
        alarmConfigService.trigger(id);
        return resultT;
    }

}
