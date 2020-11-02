package com.piesat.sso.client.controller;

import com.piesat.util.ResultT;
import com.piesat.util.ReturnCodeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/13 10:44
 */
@RestController
public class OuathController {
    @GetMapping(value = "/unauth")
    public ResultT<String> unauth() {
        ResultT<String> resultT = new ResultT<>();
        resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_401_ERROR);
        return resultT;
    }

    @GetMapping(value = "/unauthorized")
    public ResultT<String> unauthorized() {
        ResultT<String> resultT = new ResultT<>();
        resultT.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_403_ERROR);
        return resultT;
    }
}
