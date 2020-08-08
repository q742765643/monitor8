package com.piesat.skywalking.api.host;

import com.piesat.common.grpc.annotation.GrpcHthtService;
import com.piesat.common.grpc.constant.SerializeType;
import com.piesat.skywalking.dto.ProcessConfigDto;
import com.piesat.skywalking.dto.ProcessDetailsDto;
import com.piesat.util.constant.GrpcConstant;
import com.piesat.util.page.PageBean;
import com.piesat.util.page.PageForm;

import java.util.List;

@GrpcHthtService(server = GrpcConstant.SCHEDULE_SERVER,serialization = SerializeType.PROTOSTUFF)
public interface ProcessConfigService {
    public PageBean selectPageList(PageForm<ProcessConfigDto> pageForm);

    public List<ProcessConfigDto> selectBySpecification(ProcessConfigDto processConfigDto);

    public ProcessConfigDto save(ProcessConfigDto processConfigDto);

    public ProcessConfigDto findById(String id);

    public void deleteByIds(List<String> ids);

    public ProcessDetailsDto getDetail(ProcessConfigDto processConfigDto);

    public long selectCount(ProcessConfigDto processConfigDto);
}
