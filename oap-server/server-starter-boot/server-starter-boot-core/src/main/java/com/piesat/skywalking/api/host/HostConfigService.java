package com.piesat.skywalking.api.host;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.HostConfigDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface HostConfigService {
    public PageBean selectPageList(PageForm<HostConfigDto> pageForm);

    public List<HostConfigDto> selectBySpecification(HostConfigDto hostConfigdto);

    public HostConfigDto save(HostConfigDto hostConfigDto);

    public List<HostConfigDto> selectAll();

    public HostConfigDto findById(String id);

    public void deleteByIds(List<String> ids);

    public List<String> selectNosnmp();
}
