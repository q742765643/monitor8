package com.piesat.ucenter.web.controller.system;

import com.piesat.util.ResultT;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/6 11:24
 */
@RestController
@Api(value = "参数管理接口", tags = {"参数管理接口"})
@RequestMapping("/system/config")
public class ConfigController {
    @GetMapping(value = "/configKey/{configKey}")
    public ResultT<String> getConfigKey(@PathVariable String configKey) {
        ResultT<String> resultT = new ResultT<>();
        resultT.setData("111111");
        return resultT;
    }
}
