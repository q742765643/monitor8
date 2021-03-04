package com.piesat.skywalking.api.host;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;
import java.util.Map;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER, serialization = SerializeType.PROTOSTUFF)
public interface HostConfigService {
    public PageBean selectPageList(PageForm<HostConfigDto> pageForm);

    public List<HostConfigDto> selectBySpecification(HostConfigDto hostConfigdto);

    public HostConfigDto save(HostConfigDto hostConfigDto);

    public HostConfigDto updateHost(HostConfigDto hostConfigDto);

    public List<HostConfigDto> selectAll();

    public List<HostConfigDto> selectAllOrderByIp();

    public HostConfigDto findById(String id);

    public void deleteByIds(List<String> ids);

    public List<String> selectOnine();

    public List<HostConfigDto> selectAvailable();

    public List<HostConfigDto> selectOnineAll();

    public long selectCount(HostConfigDto hostConfigdto);

    public List<Map<String,Object>> findStateStatistics();

    public void trigger(String id);

    public void getUpdateTime(HostConfigDto hostConfigDto);

    public void getPacketLoss(HostConfigDto hostConfigDto);

    public List<HostConfigDto> findAllLinkIp();

    public List<HostConfigDto> findAllHostIp();

    public void upateStatus(HostConfigDto hostConfigDto);
}
