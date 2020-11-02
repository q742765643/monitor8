package com.piesat.skywalking.api.discover;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.AutoDiscoveryDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface AutoDiscoveryService {
    public List<AutoDiscoveryDto> selectBySpecification(AutoDiscoveryDto autoDiscoveryDto);

    public PageBean selectPageList(PageForm<AutoDiscoveryDto> pageForm);

    public AutoDiscoveryDto save(AutoDiscoveryDto autoDiscoveryDto);

    public AutoDiscoveryDto findById(String discoveryId);

    public void deleteByIds(List<String> ids);
}
