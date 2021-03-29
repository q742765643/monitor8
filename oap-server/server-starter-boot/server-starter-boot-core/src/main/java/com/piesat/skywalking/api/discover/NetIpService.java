package com.piesat.skywalking.api.discover;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.NetIpDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;
@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface NetIpService {
    public PageBean selectPageList(PageForm<NetIpDto> pageForm);
    public NetIpDto save(NetIpDto netIpDto);
    public NetIpDto findById(String discoveryId);
    public void deleteByIds(List<String> ids);
    public void deleteByWhere(String discoveryId);
}
