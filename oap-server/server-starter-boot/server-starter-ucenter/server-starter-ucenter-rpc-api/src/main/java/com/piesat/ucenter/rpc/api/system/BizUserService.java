package com.piesat.ucenter.rpc.api.system;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.ucenter.rpc.dto.system.BizUserDto;
import com.piesat.ucenter.rpc.dto.system.UserDto;
import com.piesat.util.ResultT;
import com.piesat.util.constant.GrpcConstant;

import java.util.List;
import java.util.Map;

/**
 * 业务注册用户
 *
 * @author cwh
 * @date 2020年 04月17日 17:25:33
 */
@GrpcHthtService(server = GrpcConstant.UCENTER_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface BizUserService {
    UserDto findByBizUserId(String userId);

    ResultT add(Map<String, String[]> parameterMap, String applyPaper);
}
