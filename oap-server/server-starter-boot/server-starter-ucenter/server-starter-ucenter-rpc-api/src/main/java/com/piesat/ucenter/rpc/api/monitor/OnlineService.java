package com.piesat.ucenter.rpc.api.monitor;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/9 14:46
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface OnlineService {
    public PageBean list(String ipaddr, String userName, int pageNum, int pageSize);
    public void forceLogout(String tokenId);
}
