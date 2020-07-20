package com.piesat.ucenter.web.controller.monitor;

import com.piesat.common.annotation.HtParam;
import com.piesat.sso.client.annotation.Log;
import com.piesat.sso.client.enums.BusinessType;
import com.piesat.ucenter.rpc.api.monitor.OnlineService;
import com.piesat.util.ResultT;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import io.swagger.annotations.Api;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/9 14:47
 */
@RestController
@Api(value = "在线用户接口", tags = {"在线用户接口"})
@RequestMapping("/monitor/online")
public class OnlineController {
    @Autowired
    private OnlineService onlineService;

    @RequiresPermissions("monitor:online:list")
    @GetMapping("/list")
    public ResultT<PageBean> list(String ipaddr, String userName,
                                  @HtParam(value="pageNum",defaultValue="1") int pageNum,
                                  @HtParam(value="pageSize",defaultValue="10") int pageSize){
        ResultT<PageBean> resultT=new ResultT<>();
        PageBean pageBean=onlineService.list(ipaddr,userName,pageNum,pageSize);
        resultT.setData(pageBean);
        return resultT;
    }

    /**
     * 强退用户
     */
    @RequiresPermissions("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tokenId}")
    public ResultT<String> forceLogout(@PathVariable String tokenId)
    {
        ResultT<String> resultT=new ResultT<>();
        onlineService.forceLogout(tokenId);
        return resultT;
    }
}
