package com.piesat.skywalking.api.discover;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.skywalking.dto.NetDiscoveryDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface NetDiscoveryService {
    public List<NetDiscoveryDto> selectBySpecification(NetDiscoveryDto netDiscoveryDto);

    public PageBean selectPageList(PageForm<NetDiscoveryDto> pageForm);

    public NetDiscoveryDto save(NetDiscoveryDto netDiscoveryDto);

    public NetDiscoveryDto findById(String discoveryId);

    public void deleteByIds(List<String> ids);

    public NetDiscoveryDto updateAutoDiscovery(NetDiscoveryDto netDiscoveryDto);

    public void trigger(String id);
}
